package com.fomat.newsonline

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import java.io.File

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    private val REQUEST_CODE = 13
    private val FILE_NAME = "photo.jpg"
    private lateinit var filePhoto: File
    private lateinit var contxt : Context
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view : View = inflater.inflate(R.layout.fragment_profile, container, false)
        var btnPicture : Button = view.findViewById(R.id.btnPicture)
        imageView = view.findViewById(R.id.imageView)
        contxt = requireContext()

        btnPicture.setOnClickListener {
            openDialog()
            /*val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            filePhoto = getPhotoFile(FILE_NAME)

            val providerFile =
                FileProvider.getUriForFile(contxt,"com.fomat.newsonline.fileprovider", filePhoto)
            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, providerFile)
            if (takePhotoIntent.resolveActivity(contxt.packageManager) != null){
                startActivityForResult(takePhotoIntent, REQUEST_CODE)
            }else {
                Toast.makeText(contxt,"Camera could not open", Toast.LENGTH_SHORT).show()
            }*/
        }

        return view
    }

    private fun getPhotoFile(fileName: String): File {
        val directoryStorage = contxt.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", directoryStorage)
    }

    private fun chooseImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_CHOOSE)
    }

    private fun openCamera(){
        val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        filePhoto = getPhotoFile(FILE_NAME)

        val providerFile =
            FileProvider.getUriForFile(contxt,"com.fomat.newsonline.fileprovider", filePhoto)
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, providerFile)
        if (takePhotoIntent.resolveActivity(contxt.packageManager) != null){
            startActivityForResult(takePhotoIntent, REQUEST_CODE)
        }else {
            Toast.makeText(contxt,"Camera could not open", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK){
            val takenPhoto = BitmapFactory.decodeFile(filePhoto.absolutePath)
            imageView.setImageBitmap(takenPhoto)
        }
        else {
            super.onActivityResult(requestCode, resultCode, data)
        }
        if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK){
            imageView.setImageURI(data?.data)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    chooseImageGallery()
                }else{
                    Toast.makeText(contxt,"Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun openDialog() {
        val openDialog = AlertDialog.Builder(contxt)
        openDialog.setIcon(R.drawable.ic_image)
        openDialog.setTitle("Choose your Image in...!!")
        openDialog.setPositiveButton("Camera"){
                dialog,_->
            openCamera()
            dialog.dismiss()

        }
        openDialog.setNegativeButton("Gallery"){
                dialog,_->
            chooseImageGallery()
            dialog.dismiss()
        }
        openDialog.setNeutralButton("Cancel"){
                dialog,_->
            dialog.dismiss()
        }
        openDialog.create()
        openDialog.show()
    }

    companion object {
        private val IMAGE_CHOOSE = 1000;
        private val PERMISSION_CODE = 1001;
    }
}