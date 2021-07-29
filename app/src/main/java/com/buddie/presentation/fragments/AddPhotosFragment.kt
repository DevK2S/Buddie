package com.buddie.presentation.fragments

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.buddie.BuildConfig
import com.buddie.adapters.AddPhotoAdapter
import com.buddie.databinding.FragmentAddPhotosBinding
import com.buddie.presentation.base.BaseFragment
import com.buddie.presentation.viewmodel.ProfileViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class AddPhotosFragment : BaseFragment(), AddPhotoAdapter.OnFabBtnClickListener {

    private lateinit var binding: FragmentAddPhotosBinding
    private lateinit var adapter: AddPhotoAdapter
    private val profileViewModel: ProfileViewModel by activityViewModels()

    var imagePosition: Int = 0
    protected val CAMERA_REQUEST = 0
    protected val GALLERY_PICTURE = 1
    private val pictureActionIntent: Intent? = null
    var bitmap: Bitmap? = null

    var selectedImagePath: String? = null

    lateinit var photoList: ArrayList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddPhotosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        photoList = ArrayList<String>()
        photoList.add("1")
        photoList.add("2")
        photoList.add("3")
        photoList.add("4")
        photoList.add("5")
        photoList.add("6")
        adapter = AddPhotoAdapter(photoList, this)
        binding.rvPhotoGrid.adapter = adapter

        binding.btnSaveImages.setOnClickListener { Navigation.findNavController(view).navigateUp() }

    }

    override fun onFabBtnClick(position: Int, content: String) {

        imagePosition = position
        if (content.equals("DeleteImage")) {
            deleteImage()
        } else
            startDialog()
    }

    private fun deleteImage() {
        photoList[imagePosition] = (imagePosition + 1).toString()
        adapter.submitList(photoList)
        adapter.notifyDataSetChanged()

    }

    private fun startDialog() {
        val myAlertDialog: AlertDialog.Builder = AlertDialog.Builder(
            activity
        )
        selectedImagePath = null
        myAlertDialog.setTitle("Upload Pictures Option")
        myAlertDialog.setMessage("How do you want to set your picture?")
        myAlertDialog.setPositiveButton("Gallery",
            DialogInterface.OnClickListener { arg0, arg1 ->
                var pictureActionIntent: Intent? = null
                pictureActionIntent = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                startActivityForResult(
                    pictureActionIntent,
                    GALLERY_PICTURE
                )
            })
        myAlertDialog.setNegativeButton("Camera",
            DialogInterface.OnClickListener { arg0, arg1 ->
                val intent = Intent(
                    MediaStore.ACTION_IMAGE_CAPTURE
                )
                intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1)
                intent.putExtra("com.google.assistant.extra.USE_FRONT_CAMERA", true)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true)
                intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1)
                intent.putExtra("android.intent.extras.CAMERA_FACING", 1)
                intent.putExtra("camerafacing", "front")
                intent.putExtra("previous_mode", "front")
                val f = createImageFile()
                intent.putExtra(
                    MediaStore.EXTRA_OUTPUT,
                    FileProvider.getUriForFile(
                        requireContext(),
                        BuildConfig.APPLICATION_ID + ".provider",
                        f
                    )
                )
                startActivityForResult(
                    intent,
                    CAMERA_REQUEST
                )
            })
        myAlertDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        bitmap = null
        if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST) {
            Log.i("Image Path", selectedImagePath!!)
            try {
                bitmap = BitmapFactory.decodeFile(selectedImagePath)
                bitmap = Bitmap.createScaledBitmap(bitmap!!, 400, 400, true)
                var rotate = 0
                try {
                    val exif = ExifInterface(selectedImagePath!!)
                    val orientation: Int = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL
                    )
                    when (orientation) {
                        ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270
                        ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180
                        ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                val matrix = Matrix()
                matrix.postRotate(rotate.toFloat())
                bitmap = Bitmap.createBitmap(
                    bitmap!!, 0, 0, bitmap!!.getWidth(),
                    bitmap!!.getHeight(), matrix, true
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }

        } else if (resultCode == RESULT_OK && requestCode == GALLERY_PICTURE) {
            if (data != null) {
                val selectedImage: Uri? = data.data
                val filePath = arrayOf(MediaStore.Images.Media.DATA)
                val c: Cursor? = selectedImage?.let {
                    requireContext().contentResolver.query(
                        it, filePath,
                        null, null, null
                    )
                }
                c?.moveToFirst()
                val columnIndex = c!!.getColumnIndex(filePath[0])
                selectedImagePath = c?.getString(columnIndex)
                c?.close()

            }
        }
        setImage()
    }

    private fun setImage() {
        if (selectedImagePath != null) {
            Toast.makeText(requireContext(), selectedImagePath, Toast.LENGTH_SHORT).show()
            photoList[imagePosition] = selectedImagePath!!
            adapter.submitList(photoList)
            adapter.notifyDataSetChanged()
            Log.i("Image", "Added " + photoList.toString())
        }

    }

    private fun createImageFile(): File {

        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            selectedImagePath = absolutePath
        }
    }


}