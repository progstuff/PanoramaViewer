package project.projectfive.panoramaviewerproject.OpenGLClasses.OpenGLExamples

import android.content.Context
import android.content.res.Resources
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

object FileUtils {
    fun readTextFromRaw(context: Context?, resourceId: Int): String {
        val stringBuilder = StringBuilder()
        try {
            var bufferedReader: BufferedReader? = null
            try {
                if(context != null) {
                    val inputStream: InputStream =
                        context.getResources().openRawResource(resourceId)
                    bufferedReader = BufferedReader(InputStreamReader(inputStream))
                    var line: String? = ""
                    while (bufferedReader.readLine().also({ line = it }) != null) {
                        stringBuilder.append(line)
                        stringBuilder.append("\r\n")
                    }
                }
            } finally {
                if (bufferedReader != null) {
                    bufferedReader.close()
                }
            }
        } catch (ioex: IOException) {
            ioex.printStackTrace()
        } catch (nfex: Resources.NotFoundException) {
            nfex.printStackTrace()
        }
        return stringBuilder.toString()
    }
}