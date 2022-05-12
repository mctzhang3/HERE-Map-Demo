package com.mzhang.heremapdemo.locationLanding.view

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.here.sdk.core.Anchor2D
import com.here.sdk.core.GeoCoordinates
import com.here.sdk.core.Metadata
import com.here.sdk.mapview.MapImageFactory
import com.here.sdk.mapview.MapMarker
import com.here.sdk.mapview.MapScheme
import com.here.sdk.mapview.MapView
import com.mzhang.heremapdemo.R
import com.mzhang.heremapdemo.databinding.FragmentFamilyMemberMapBinding
import com.mzhang.heremapdemo.locationLanding.adapter.FamilyMemberLocationAdapter
import com.mzhang.heremapdemo.locationLanding.model.MemberInfo
import com.mzhang.heremapdemo.locationLanding.model.SampleFamilyMembers
import com.mzhang.heremapdemo.locationLanding.model.SampleFamilyMembers.MAP_CENTER_GEO_COORDINATES
import com.mzhang.heremapdemo.locationLanding.utils.Constants.FAMILY_MAP_MARKER_BACKGROUND_IMAGE_HEIGHT
import com.mzhang.heremapdemo.locationLanding.utils.Constants.FAMILY_MAP_MARKER_BACKGROUND_IMAGE_WIDTH
import com.mzhang.heremapdemo.locationLanding.utils.Constants.FAMILY_MAP_MARKER_FOREGROUND_IMAGE_HEIGHT
import com.mzhang.heremapdemo.locationLanding.utils.Constants.FAMILY_MAP_MARKER_FOREGROUND_IMAGE_WIDTH
import com.mzhang.heremapdemo.locationLanding.utils.Constants.TEXT_SIZE_ON_BITMAP
import com.mzhang.heremapdemo.locationLanding.utils.Constants.TOP_IMAGE_X
import com.mzhang.heremapdemo.locationLanding.utils.Constants.TOP_IMAGE_Y
import com.mzhang.heremapdemo.locationLanding.utils.PermissionsRequestor
import com.mzhang.heremapdemo.locationLanding.utils.Utils

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FamilyMemberMapFragment : Fragment() {

    private val TAG: String = FamilyMemberMapFragment::class.java.simpleName
    private var _binding: FragmentFamilyMemberMapBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var permissionsRequestor: PermissionsRequestor? = null
    private var mapView: MapView? = null
    private val mapMarkerList: MutableList<MapMarker> = ArrayList()
    private val memberList: List<MemberInfo> = SampleFamilyMembers.getSampleFamilyMemberInfo()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFamilyMemberMapBinding.inflate(inflater, container, false)

        // Get a MapView instance from layout
        mapView = binding.mapView
        mapView?.onCreate(savedInstanceState)

        handleAndroidPermissions()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBottomSheet()
        initRecyclerView()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    private fun setupBottomSheet() {
        BottomSheetBehavior.from(binding.inLocBottomSheet.bottomSheet).apply {
            state = BottomSheetBehavior.STATE_COLLAPSED
        }

    }

    private fun initRecyclerView() {
        binding.inLocBottomSheet.rcMemberList.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = FamilyMemberLocationAdapter(memberList)
        }
    }


    private fun handleAndroidPermissions() {
        permissionsRequestor = PermissionsRequestor(requireActivity())
        permissionsRequestor?.request(object : PermissionsRequestor.ResultListener {
            override fun permissionsGranted() {
                loadMapScene()
            }

            override fun permissionsDenied() {
                Log.e(TAG, "Permissions denied by user.")
            }
        })
    }

    private fun loadMapScene() {
        mapView?.mapScene?.loadScene(
            MapScheme.NORMAL_DAY
        ) { mapError ->
            if (mapError == null) {
                val distanceInMeters = (1000 * 20).toDouble()
                mapView?.camera?.lookAt(
                    MAP_CENTER_GEO_COORDINATES,
                    distanceInMeters
                )
                showFamilyMemberOnMap()
            } else {
                Log.d(TAG, "onLoadScene failed: $mapError")
            }
        }
    }

    private fun showFamilyMemberOnMap() {
        for (member in memberList) {
            val bmp =
                Utils.getContactPhotoBitmapFromPhoneNumber(requireContext(), member.phoneNumber)

            if (bmp != null) {
                addMergedPhotoMapMarker(member.geoCoordinates, bmp)
            } else {
                showTextMarker(member)
            }
        }

        // TODO: GeoBoundingBox need paid SDK
//        val geoBoundingBox = GeoBoundingBox.getBoundingBoxContainingGeoCoordinates
    }

    private fun addMergedPhotoMapMarker(geoCoordinates: GeoCoordinates, bmp: Bitmap?) {
        val backgroundBitmap = BitmapFactory.decodeResource(
            requireContext().resources,
            R.drawable.poi
        )

        val resizeBmp = Bitmap.createScaledBitmap(
            backgroundBitmap,
            FAMILY_MAP_MARKER_BACKGROUND_IMAGE_WIDTH,
            FAMILY_MAP_MARKER_BACKGROUND_IMAGE_HEIGHT,
            false
        )

        val cropBitmap = Bitmap.createScaledBitmap(
            Utils.getCircleBitmap(bmp!!),
            FAMILY_MAP_MARKER_FOREGROUND_IMAGE_WIDTH,
            FAMILY_MAP_MARKER_FOREGROUND_IMAGE_HEIGHT,
            false
        )

        val mergedImage = Utils.createSingleBitmapFrom2Bitmaps(resizeBmp, cropBitmap, TOP_IMAGE_X, TOP_IMAGE_Y)

        val mapImage = MapImageFactory.fromBitmap(mergedImage!!)

        // The bottom, middle position should point to the location.
        // By default, the anchor point is set to 0.5, 0.5.
        val anchor2D = Anchor2D(0.5, 1.0)
        val mapMarker = MapMarker(geoCoordinates, mapImage, anchor2D)
//        val metadata = Metadata()
//        metadata.setString("key_poi", "This is a POI.")
//        mapMarker.metadata = metadata
        mapView?.mapScene?.addMapMarker(mapMarker)
        mapMarkerList.add(mapMarker)
    }

    fun showTextMarker(member: MemberInfo) {
        val name = member.nickName.substring(0, 2)
        val textBmp = Utils.drawTextOnBitmap(name, TEXT_SIZE_ON_BITMAP, Color.WHITE)

        addMergedPhotoMapMarker(member.geoCoordinates, textBmp)
    }

    private fun clearMap() {
        mapView?.mapScene?.removeMapMarkers(mapMarkerList)
        mapMarkerList.clear()
    }

}