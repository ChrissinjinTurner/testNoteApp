package com.example.notes_test_project

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.scan_notes_layout.*
import java.io.File
import java.io.FileOutputStream
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.core.app.ActivityCompat
import android.widget.Toast
import android.content.ActivityNotFoundException
import android.graphics.*
import java.io.ByteArrayOutputStream
import java.io.OutputStream

class ScanNotesActivity : AppCompatActivity() {

    private val REQUEST_FRONT_IMAGE = 1
    private val REQUEST_BACK_IMAGE = 2

    private var imageOne: Bitmap? = null
    private var imageTwo: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.scan_notes_layout)

        frontButton.setOnClickListener {
            dispatchTakePictureIntent(REQUEST_FRONT_IMAGE)
        }

        backButton.setOnClickListener {
            dispatchTakePictureIntent(REQUEST_BACK_IMAGE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_FRONT_IMAGE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            imageOne = imageBitmap
            frontImage.setImageBitmap(imageBitmap)
        } else if (requestCode == REQUEST_BACK_IMAGE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            imageTwo = imageBitmap
            backImage.setImageBitmap(imageBitmap)
        }
        if (imageOne != null && imageTwo != null) {
            // Write the PDF file to a file
            if (ContextCompat.checkSelfPermission(this as Activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this as Activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this as Activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 10)
                ActivityCompat.requestPermissions(this as Activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 10)
            } else {
                loadImagesToPDF(imageOne, imageTwo)
            }
        }
    }

    private fun dispatchTakePictureIntent(requestCode: Int) {
        when (requestCode) {
            REQUEST_FRONT_IMAGE -> {
                Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                    takePictureIntent.resolveActivity(packageManager)?.also {
                        startActivityForResult(takePictureIntent, REQUEST_FRONT_IMAGE)
                    }
                }
            }

            REQUEST_BACK_IMAGE -> {
                Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                    takePictureIntent.resolveActivity(packageManager)?.also {
                        startActivityForResult(takePictureIntent, REQUEST_BACK_IMAGE)
                    }
                }
            }

            else -> {

            }
        }
    }

    /**
     * Right now all this does is load two blurry images into a pdf. I'm not entirely sure why they
     * are blurry but I can't seem to fix it no matter what I try. It has to do with something related
     * to drawing bitmaps onto a canvas, and then putting that canvas on the page of the pdf. The images
     * look fine when they are put into an ImageView so it has to be that. Honestly though this was just
     * a side project so I don't think its super important as of now.
     */
    private fun loadImagesToPDF(imageOne: Bitmap?, imageTwo: Bitmap?) {
        if (imageOne != null && imageTwo != null) {
//            val bmTop = Bitmap.createBitmap(imageOne.width, imageOne.height, imageOne.config)
//            val canvas = Canvas(bmTop)
//            canvas.drawBitmap(imageOne, 0f, 0f, null)
//            canvas.drawBitmap(imageTwo, 0f, imageOne.height.toFloat(), null)
//
//            combinedImage.setImageBitmap(bmTop)
//
            // Create a PdfDocument with a page of the same size as the image
            val document = PdfDocument()

            val pageInfo: PdfDocument.PageInfo  = PdfDocument.PageInfo.Builder(imageOne.width, imageOne.height, 1).create()
            val page: PdfDocument.Page  = document.startPage(pageInfo)
            val canvas: Canvas = page.canvas
            canvas.drawBitmap(imageOne, 0f, 0f, Paint())
            document.finishPage(page)

            val pageInfo2: PdfDocument.PageInfo = PdfDocument.PageInfo.Builder(imageTwo.width, imageTwo.height, 2).create()
            val page2: PdfDocument.Page = document.startPage(pageInfo2)
            val canvas2: Canvas = page2.canvas
            canvas2.drawBitmap(imageTwo, 0f, 0f, Paint())
            document.finishPage(page2)

//            // Write the PDF file to a file
            val directoryPath: String =
                Environment.getExternalStorageDirectory().toString()
            document.writeTo(FileOutputStream("$directoryPath/example.pdf"))
            document.close()
//            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(directoryPath + "/example.pdf")))
            downloadPDF(File("$directoryPath/example.pdf"))
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadImagesToPDF(imageOne, imageTwo)
        } else {
            Log.d("Failed", "Download Failed")
        }
    }

    private fun downloadPDF(file: File) {
        val downloadManager =   this.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.addCompletedDownload(file.getName(), file.getName(), true, "application/pdf", file.getAbsolutePath(),file.length(),true)
    }
}