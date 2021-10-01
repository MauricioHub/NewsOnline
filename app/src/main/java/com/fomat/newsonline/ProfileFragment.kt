package com.fomat.newsonline

import android.annotation.TargetApi
import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File


/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    private val OPERATION_CAPTURE_PHOTO = 1
    private val OPERATION_CHOOSE_PHOTO = 2
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var btnPicture: Button
    private lateinit var btnSave: Button
    private lateinit var imageView: ImageView
    private lateinit var usernameEtv: EditText
    private lateinit var contxt : Context
    private var mUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view : View = inflater.inflate(R.layout.fragment_profile, container, false)
        btnPicture = view.findViewById(R.id.btnPicture)
        btnSave = view.findViewById(R.id.btnSave)
        imageView = view.findViewById(R.id.imageView)
        usernameEtv = view.findViewById(R.id.usernameEtv)
        contxt = requireContext()
        firebaseAuth = FirebaseAuth.getInstance()

        btnPicture.setOnClickListener {
            openDialog()
        }

        btnSave.setOnClickListener {
            updateProfile()
        }
        return view
    }

    private fun updateProfile(){
        if (mUri != null){
            firebaseAuth.currentUser.let { user ->
                val username = usernameEtv.text.toString()
                val photoURI = Uri.parse(mUri.toString())
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .setPhotoUri(photoURI)
                    .build()

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        if (user != null) {
                            user.updateProfile(profileUpdates)
                        }
                        withContext(Dispatchers.Main){
                            Toast.makeText(contxt, "Perfil de usuario actualizado de manera satisfactoria.",
                                Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception){
                        withContext(Dispatchers.Main){
                            Toast.makeText(contxt, e.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        } else{
            Toast.makeText(contxt, "Debe cargar foto primero!", Toast.LENGTH_LONG).show()
        }
    }

    private fun gallerySelected(){
        //check permission at runtime
        val checkSelfPermission = ContextCompat.checkSelfPermission(contxt,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (checkSelfPermission != PackageManager.PERMISSION_GRANTED){
            //Requests permissions to be granted to this application at runtime
            ActivityCompat.requestPermissions(
                contxt as Activity,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }
        else{
            openGallery()
        }
    }

    private fun openCamera(){
        val capturedImage = File(contxt.externalCacheDir, "My_Captured_Photo.jpg")
        if(capturedImage.exists()) {
            capturedImage.delete()
        }
        capturedImage.createNewFile()
        mUri = if(Build.VERSION.SDK_INT >= 24){
            FileProvider.getUriForFile(contxt, "com.fomat.newsonline.fileprovider",
                capturedImage)
        } else {
            Uri.fromFile(capturedImage)
        }

        val intent = Intent("android.media.action.IMAGE_CAPTURE")
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mUri)
        startActivityForResult(intent, OPERATION_CAPTURE_PHOTO)
    }

    private fun openGallery(){
        val intent = Intent("android.intent.action.GET_CONTENT")
        intent.type = "image/*"
        startActivityForResult(intent, OPERATION_CHOOSE_PHOTO)
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver,
            inImage, "Title", null)
        return Uri.parse(path)
    }

    private fun renderImage(imagePath: String?){
        if (imagePath != null) {
            val bitmap = BitmapFactory.decodeFile(imagePath)
            mUri = getImageUri(contxt, bitmap)
            imageView?.setImageBitmap(bitmap)
        }
        else {
            show("ImagePath is null")
        }
    }

    private fun getImagePath(uri: Uri?, selection: String?): String {
        var path: String? = null
        val cursor = uri?.let { contxt.contentResolver.query(it, null, selection, null, null ) }
        if (cursor != null){
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            }
            cursor.close()
        }
        return path!!
    }

    @TargetApi(19)
    private fun handleImageOnKitkat(data: Intent?) {
        var imagePath: String? = null
        val uri = data!!.data
        mUri = uri
        //DocumentsContract defines the contract between a documents provider and the platform.
        if (DocumentsContract.isDocumentUri(contxt, uri)){
            val docId = DocumentsContract.getDocumentId(uri)
            if (uri != null) {
                if ("com.android.providers.media.documents" == uri.authority){
                    val id = docId.split(":")[1]
                    val selsetion = MediaStore.Images.Media._ID + "=" + id
                    imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        selsetion)
                } else if ("com.android.providers.downloads.documents" == uri.authority){
                    val contentUri = ContentUris.withAppendedId(Uri.parse(
                        "content://downloads/public_downloads"), java.lang.Long.valueOf(docId))
                    imagePath = getImagePath(contentUri, null)
                }
            }
        }
        else if (uri != null) {
            if ("content".equals(uri.scheme, ignoreCase = true)){
                imagePath = getImagePath(uri, null)
            }
            else if ("file".equals(uri.scheme, ignoreCase = true)){
                imagePath = uri.path
            }
        }
        renderImage(imagePath)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>
                                            , grantedResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantedResults)
        when(requestCode){
            1 ->
                if (grantedResults.isNotEmpty() && grantedResults.get(0) ==
                    PackageManager.PERMISSION_GRANTED){
                    openGallery()
                }else {
                    show("Unfortunately You are Denied Permission to Perform this Operataion.")
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            OPERATION_CAPTURE_PHOTO ->
                if (resultCode == Activity.RESULT_OK) {
                    val bitmap = BitmapFactory.decodeStream(
                        mUri?.let { contxt.getContentResolver().openInputStream(it) })
                    imageView!!.setImageBitmap(bitmap)
                }
            OPERATION_CHOOSE_PHOTO ->
                if (resultCode == Activity.RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= 19) {
                        handleImageOnKitkat(data)
                    }
                }
        }
    }

    private fun show(message: String) {
        Toast.makeText(contxt,message,Toast.LENGTH_SHORT).show()
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
            gallerySelected()
            dialog.dismiss()
        }
        openDialog.setNeutralButton("Cancel"){
                dialog,_->
            dialog.dismiss()
        }
        openDialog.create()
        openDialog.show()
    }
}