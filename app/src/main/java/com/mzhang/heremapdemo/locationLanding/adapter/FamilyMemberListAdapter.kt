package com.mzhang.heremapdemo.locationLanding.adapter

import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mzhang.heremapdemo.databinding.MemberLocationListItemBinding
import com.mzhang.heremapdemo.locationLanding.model.MemberInfo
import com.mzhang.heremapdemo.locationLanding.utils.Utils


class FamilyMemberLocationAdapter(private val memberInfoList: List<MemberInfo>?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = MemberLocationListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val memberInfo = memberInfoList?.get(position)
        (holder as ViewHolder).bind(memberInfo)
    }

    override fun getItemCount(): Int {
        return memberInfoList?.size?: 0
    }

    data class ViewHolder(private val binding: MemberLocationListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(memberInfo: MemberInfo?) {
            val bmp = Utils.getContactPhotoBitmapFromPhoneNumber(binding.root.context, memberInfo?.phoneNumber)
            memberInfo?.apply {
                binding.tvMemberLocation.text = nearLocation
                binding.tvMemberName.text = nickName
                binding.tvMemberTimeSince.text = timeSince
                if (bmp != null) {
                    binding.tvMemberShortName.background = BitmapDrawable(binding.root.context.resources, bmp)
                } else {
                    binding.tvMemberShortName.text = nickName.substring(0, 2)
                }
            }
        }
    }
}