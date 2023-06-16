package com.example.sqlite
import SQLiteHelper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.ByteArrayOutputStream

class UserInputActivity : AppCompatActivity() {
    private lateinit var ivImage: ImageView
    private lateinit var Name: EditText
    private lateinit var PNumber: EditText
    private lateinit var btnAdd: Button

    private lateinit var defaultImageBitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_input)

        ivImage = findViewById(R.id.ivImage)
        Name = findViewById(R.id.Name)
        PNumber = findViewById(R.id.Pnumber)
        btnAdd = findViewById(R.id.btnAdd)
        defaultImageBitmap = BitmapFactory.decodeResource(resources, R.drawable.addimage)
        btnAdd.setOnClickListener {
            saveData()
        }

        ivImage.setOnClickListener {
            val imageIntent = Intent(Intent.ACTION_PICK)
            imageIntent.type = "image/*"
            startActivityForResult(imageIntent, REQUEST_IMAGE_PICK)
        }
    }

    private fun saveData() {
        val name = Name.text.toString().trim()
        val numberr = PNumber.text.toString().trim()

        if (name.isEmpty() || numberr.isEmpty()) {
            Toast.makeText(this, "Please enter both name and number", Toast.LENGTH_SHORT).show()
            return
        }

        val number = numberr.toIntOrNull()
        if (number == null || number <= 0) {
            Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show()
            return
        }

        val imageBitmap = if (ivImage.drawable != null) {
            (ivImage.drawable as BitmapDrawable).bitmap
        } else {
            defaultImageBitmap
        }

        val imageByteArray = getImageByteArray(imageBitmap)

        val dbHelper = SQLiteHelper(this)
        val item = ItemModel(name = name, phonenumber = number, image = imageByteArray)
        val success = dbHelper.insertItem(item)

        if (success != -1L) {
            Toast.makeText(this, "Contact is added successfully", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            startActivity(Intent(this,MainActivity::class.java))
        } else {
            Toast.makeText(this, "Failed to add the Number", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getImageByteArray(image: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            data?.data?.let { imageUri ->
                try {
                    val inputStream = contentResolver.openInputStream(imageUri)
                    val selectedImageBitmap = BitmapFactory.decodeStream(inputStream)
                    ivImage.setImageBitmap(selectedImageBitmap)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MainActivity::class.java))
    }

    companion object {
        private const val REQUEST_IMAGE_PICK = 1
    }
}
