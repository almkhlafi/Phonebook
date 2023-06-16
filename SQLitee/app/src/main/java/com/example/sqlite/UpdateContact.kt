package com.example.sqlite

import SQLiteHelper
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.ByteArrayOutputStream

class UpdateContact : AppCompatActivity() {
    private lateinit var sqliteHelper: SQLiteHelper
    private lateinit var Name: EditText
    private lateinit var Pnumber: EditText
    private lateinit var ivImage: ImageView
    private lateinit var btnUpdate: Button
    private var selectedImageByteArray: ByteArray? = null
    private var selectedItem: ItemModel? = null

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_GALLERY_ACCESS = 2
        private const val PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 3
        var imgValidation = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_contact)
        initView()
        sqliteHelper = SQLiteHelper(this)

        val itemId = intent.getIntExtra("itemId", -1)
        if (itemId != -1) {
            selectedItem = sqliteHelper.getItemByID(itemId)
            if (selectedItem != null) {
                Name.setText(selectedItem!!.name)
                Pnumber.setText(selectedItem!!.phonenumber.toString())

                selectedItem!!.image?.let { imgByteArray ->
                    selectedImageByteArray = imgByteArray
                    val bitmap = BitmapFactory.decodeByteArray(imgByteArray, 0, imgByteArray.size)
                    ivImage.setImageBitmap(bitmap)
                }
            } else {
                Toast.makeText(this, "Item not found", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            Toast.makeText(this, "Invalid item ID", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnUpdate.setOnClickListener {
            updateItem()
        }

        ivImage.setOnClickListener {
            showImageOptions()
        }
    }

    private fun initView() {
        Name = findViewById(R.id.Name)
        Pnumber = findViewById(R.id.Pnumber)
        ivImage = findViewById(R.id.ivImage)
        btnUpdate = findViewById(R.id.btnUpdate)
    }

    private fun updateItem() {
        val name = Name.text.toString()
        val number = Pnumber.text.toString().toInt()

        if (name == selectedItem?.name && number == selectedItem?.phonenumber && imgValidation == 0) {
            Toast.makeText(this, "Record not changed", Toast.LENGTH_LONG).show()
            return
        }

        if (selectedItem == null) {
            return
        }
        val updatedItem = ItemModel(
            id = selectedItem!!.id,
            name = name,
            phonenumber = number,
            image = selectedItem?.image ?: selectedImageByteArray
        )
        val status = sqliteHelper.updateItem(updatedItem)

        if (status > -1) {
            Toast.makeText(this, "Item updated", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish() // Close the activity after updating the item
        } else {
            Toast.makeText(this, "Failed to update item", Toast.LENGTH_LONG).show()
        }
    }

    private fun showImageOptions() {
        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Add Photo")
        builder.setItems(options) { dialog, item ->
            when {
                options[item] == "Take Photo" -> {
                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE)
                }
                options[item] == "Choose from Gallery" -> {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            PERMISSION_REQUEST_READ_EXTERNAL_STORAGE
                        )
                    } else {
                        openGallery()
                    }
                }
                options[item] == "Cancel" -> dialog.dismiss()
            }
        }
        imgValidation++
        builder.show()
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, REQUEST_GALLERY_ACCESS)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as? Bitmap
            if (imageBitmap != null) {
                val imageByteArray = getImageByteArray(imageBitmap)
                selectedImageByteArray = imageByteArray
                ivImage.setImageBitmap(imageBitmap)
            } else {
                Toast.makeText(this, "Failed to retrieve image", Toast.LENGTH_LONG).show()
            }
        } else if (requestCode == REQUEST_GALLERY_ACCESS && resultCode == RESULT_OK) {
            val selectedImageUri = data?.data
            if (selectedImageUri != null) {
                val imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImageUri)
                val imageByteArray = getImageByteArray(imageBitmap)
                selectedImageByteArray = imageByteArray
                ivImage.setImageBitmap(imageBitmap)
            } else {
                Toast.makeText(this, "Failed to retrieve image", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getImageByteArray(imageBitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else {
                Toast.makeText(
                    this,
                    "Permission denied. Cannot access gallery.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MainActivity::class.java))
    }
}
