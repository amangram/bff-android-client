package kg.bff.client.ui.profile

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kg.bff.client.*
import kg.bff.client.data.model.State
import kg.bff.client.ui.GlideApp
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.ByteArrayOutputStream
import java.util.*

class EditProfileFragment : Fragment() {
    companion object {
        fun newInstance(): EditProfileFragment = EditProfileFragment()
    }

    private val myViewModel: ProfileViewModel by viewModel()
    private lateinit var selectedImageBytes: Array<ByteArray>
    private var pictureJustChanged = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setData()
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        tv_edit_user_birthdate.setOnClickListener {
            val datePicker =
                DatePickerDialog(
                    requireContext(),
                    DatePickerDialog.OnDateSetListener { _, mYear, mMonth, mDay ->
                        tv_edit_user_birthdate.text = "$mDay/$mMonth/$mYear"
                    },
                    year,
                    month,
                    day
                )
            datePicker.show()
        }
        edit_profile_image_imageView.setOnClickListener {
            CropImage.startPickImageActivity(requireContext(), this)
        }
        save_btn.setOnClickListener {
            if (::selectedImageBytes.isInitialized)
                myViewModel.updateUser(
                    et_edit_user_name.text.toString(),
                    tv_edit_user_birthdate.text.toString(), selectedImageBytes
                )
            else
                myViewModel.updateUser(
                    et_edit_user_name.text.toString(),
                    tv_edit_user_birthdate.text.toString()
                )
            activity?.toast("Saving")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE) {
            val croppedImagePath = data!!.data
            cropRequest(croppedImagePath!!)
            pictureJustChanged = true
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            val croppedImageBmp = MediaStore.Images.Media
                .getBitmap(activity?.contentResolver, result.uri)
            val originalOutputStream = ByteArrayOutputStream()
            val previewOutputStream = ByteArrayOutputStream()
            croppedImageBmp.compress(Bitmap.CompressFormat.JPEG, 80, originalOutputStream)
            croppedImageBmp.compress(Bitmap.CompressFormat.JPEG, 40, previewOutputStream)
            val originalImageBytes = originalOutputStream.toByteArray()
            val previewImageBytes = previewOutputStream.toByteArray()
            selectedImageBytes = arrayOf(originalImageBytes, previewImageBytes)
            GlideApp.with(this)
                .load(selectedImageBytes[0])
                .into(edit_profile_image_imageView)
        }
    }

    private fun setData() {
        myViewModel.user.observe(viewLifecycleOwner, Observer {state->
            when(state){
                is State.Loading->{
                    pb_edit_profile.visible()
                }
                is State.Success->{
                    pb_edit_profile.gone()
                    state.data.let { user ->
                        et_edit_user_name.setText(user.name)
                        tv_edit_user_birthdate.text = user.birthdate
                        if (!pictureJustChanged){
                            user.image?.preview.let { preview->
                                GlideApp.with(this)
                                    .load(preview)
                                    .placeholder(R.drawable.ic_account_circle_black_24dp)
                                    .into(edit_profile_image_imageView)
                            }
                        }
                    }
                }
            }
        })
    }

    private fun cropRequest(imageUri: Uri) {
        CropImage.activity(imageUri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setMultiTouchEnabled(true)
            .setMaxCropResultSize(2000, 3000)
            .start(requireContext(), this)
    }
}