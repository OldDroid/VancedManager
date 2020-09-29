package com.vanced.manager.ui.fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.google.android.material.button.MaterialButton
import com.vanced.manager.R
import com.vanced.manager.utils.DownloadHelper.downloadManager
import com.vanced.manager.utils.InternetTools.isUpdateAvailable
import kotlinx.coroutines.runBlocking

class UpdateCheckFragment : DialogFragment() {

    //private var downloadId: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (dialog != null && dialog?.window != null) {
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        return inflater.inflate(R.layout.fragment_update_check, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //activity?.registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        checkUpdates()
        view.findViewById<Button>(R.id.update_center_dismiss).setOnClickListener { dismiss() }
        view.findViewById<MaterialButton>(R.id.update_center_recheck).setOnClickListener{ checkUpdates() }

        downLoadingState(false)
    }

    /*
    override fun onPause() {
        super.onPause()
        activity?.unregisterReceiver(receiver)
    }
     */

    private fun checkUpdates() {
        val updatebtn = view?.findViewById<Button>(R.id.update_center_update)
        val checkingTxt = view?.findViewById<TextView>(R.id.update_center_checking)

        runBlocking {
            if (isUpdateAvailable()) {
                view?.findViewById<Button>(R.id.update_center_recheck)?.visibility = View.GONE
                checkingTxt?.text = getString(R.string.update_found)

                updatebtn?.setOnClickListener {
                    downLoadingState(true)
                    downloadManager(false, requireActivity(), view?.findViewById(R.id.update_center_progressbar))
                    downLoadingState(false)
                }
            } else
                checkingTxt?.text = getString(R.string.update_notfound)
        }
    }

    private fun downLoadingState(isDownloading: Boolean) {
        if (isDownloading) {
            dialog?.setCancelable(false)
            view?.findViewById<MaterialButton>(R.id.update_center_update)?.isEnabled = false
        }
        else {
            dialog?.setCancelable(true)
            view?.findViewById<MaterialButton>(R.id.update_center_update)?.isEnabled = true
        }
    }

    /*

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1) == downloadId) {
                activity?.let {
                    val apk = File("${activity?.getExternalFilesDir("apk")}/manager.apk")
                    val uri =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                            FileProvider.getUriForFile(activity!!, "${activity?.packageName}.provider", apk)
                        else
                            Uri.fromFile(apk)

                    val mIntent = Intent(Intent.ACTION_VIEW)
                    mIntent.setDataAndType(uri, "application/vnd.android.package-archive")
                    mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    mIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    startActivity(mIntent)
                }
            }
        }

    }
     */



}
