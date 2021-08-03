package com.example.firsttensorflowliteapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.FileChannel


class MainActivity : AppCompatActivity() {

    lateinit var result: TextView
    private lateinit var button: Button
    lateinit var editText: EditText
    var interpreter : Interpreter? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editText = findViewById(R.id.editTextTextPersonName)
        result = findViewById(R.id.Text)
        button = findViewById(R.id.Button)

        try {
            interpreter = Interpreter(loadModelFile(), null)
        } catch (e: IOException) {
            e.printStackTrace()
        }


        button.setOnClickListener {
            val finalOutput: Float = doInference(editText.text.toString())
            result.text = "Result: $finalOutput"
        }


    }

    //This function will open our tflite file and convert it into MappedByteBuffer format
    //MappedByteBuffer is optimized and fast soo we are using it
    @Throws(IOException::class)
    private fun loadModelFile(): ByteBuffer {
        val assetFileDescriptor = this.assets.openFd("linear.tflite")
        val fileInputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel: FileChannel = fileInputStream.getChannel()
        val startOffset = assetFileDescriptor.startOffset
        val length = assetFileDescriptor.length
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, length)
    }


    private fun doInference(str: String): Float {
        val input = FloatArray(1)
        input[0] = str.toFloat()

        val output = Array(1) {
            FloatArray(
                1
            )
        }
        interpreter!!.run(input, output)
        return output[0][0]
    }


}