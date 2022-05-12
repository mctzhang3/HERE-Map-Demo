package com.mzhang.heremapdemo.locationLanding.utils

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.provider.ContactsContract.PhoneLookup
import android.provider.MediaStore


object Utils {

    fun drawTextOnBitmap(text: String, textSize: Float, textColor: Int): Bitmap? {
        val paint = Paint(ANTI_ALIAS_FLAG)
        paint.textSize = textSize
        paint.color = textColor
        paint.textAlign = Paint.Align.CENTER
        val baseline = -paint.ascent() // ascent() is negative
        var width = (paint.measureText(text) + textSize).toInt()  // add padding
        var height = (baseline + paint.descent() + textSize).toInt()
        if (width > height) height = width else width = height
        val image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(image)
        // draw background color
        canvas.drawColor(Color.BLACK)
        canvas.drawText(text, (width/2).toFloat(), baseline + textSize/2, paint)
        return image
    }
    fun createSingleBitmapFrom2Bitmaps(
        firstImage: Bitmap,
        secondImage: Bitmap,
        left: Float,
        top: Float
    ): Bitmap? {
        val result = Bitmap.createBitmap(firstImage.width, firstImage.height, firstImage.config)
        val canvas = Canvas(result)
        canvas.drawBitmap(firstImage, 0f, 0f, null)
        canvas.drawBitmap(secondImage, left, top, null)
        return result
    }

    fun getCircleBitmap(bitmap: Bitmap): Bitmap {
        val targetBmp: Bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, false)

        val output = Bitmap.createBitmap(
            targetBmp.width,
            targetBmp.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(output)
        val color = 0xff424242
        val paint = Paint()
        val rect = Rect(0, 0, targetBmp.width, targetBmp.height)
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color.toInt()
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(
            targetBmp.width / 2.0f, targetBmp.height / 2.0f,
            targetBmp.width / 2.0f, paint
        )
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(targetBmp, rect, rect, paint)
        return output
    }

    fun getContactPhotoBitmapFromPhoneNumber(context: Context, phoneNumber: String?): Bitmap? {
        val uri = getContactPhotoUriFromPhoneNumber(context, phoneNumber)

        return if (uri == null) {
            null
        } else
            when {
                Build.VERSION.SDK_INT < 28 -> {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }
                else -> {
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    ImageDecoder.decodeBitmap(source)
                }
            }
    }

    fun getContactPhotoUriFromPhoneNumber(context: Context, phoneNumber: String?): Uri? {
        val contactId = fetchContactIdFromPhoneNumber(context, phoneNumber)?.toLong()
        if (contactId != null) {
            return getPhotoUri(context, contactId)
        }
        return null
    }

    fun fetchContactIdFromPhoneNumber(context: Context, phoneNumber: String?): String? {
        val uri = Uri.withAppendedPath(
            PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(phoneNumber)
        )
        val cursor: Cursor? = context.contentResolver.query(
            uri, arrayOf(PhoneLookup.DISPLAY_NAME, PhoneLookup._ID),
            null, null, null
        )
        var contactId: String? = ""
        if (cursor != null) {
            while (cursor.moveToNext()) {
                contactId =
                    cursor.getString(cursor.getColumnIndexOrThrow(PhoneLookup._ID))
            }
            cursor.close()
        }
        return contactId
    }

    fun getPhotoUri(context: Context, contactId: Long): Uri? {
        val contentResolver: ContentResolver = context.contentResolver
        try {
            val cursor = contentResolver
                .query(
                    ContactsContract.Data.CONTENT_URI,
                    null,
                    ContactsContract.Data.CONTACT_ID
                            + "="
                            + contactId
                            + " AND "
                            + ContactsContract.Data.MIMETYPE
                            + "='"
                            + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE
                            + "'", null, null
                )
            if (cursor != null) {
                if (!cursor.moveToFirst()) {
                    return null // no photo
                }
                cursor.close()
            } else {
                return null // error in cursor process
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        val person = ContentUris.withAppendedId(
            ContactsContract.Contacts.CONTENT_URI, contactId
        )
        return Uri.withAppendedPath(
            person,
            ContactsContract.Contacts.Photo.CONTENT_DIRECTORY
        )
    }
}