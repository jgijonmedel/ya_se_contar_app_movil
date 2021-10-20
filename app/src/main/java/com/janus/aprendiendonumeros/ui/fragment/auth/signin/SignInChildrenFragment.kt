package com.janus.aprendiendonumeros.ui.fragment.auth.signin

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.children
import androidx.fragment.app.viewModels
import com.janus.aprendiendonumeros.R
import com.janus.aprendiendonumeros.core.AESCrypt
import com.janus.aprendiendonumeros.core.Resource
import com.janus.aprendiendonumeros.core.TextProvider
import com.janus.aprendiendonumeros.data.remote.AuthDataSource
import com.janus.aprendiendonumeros.databinding.FragmentSignInChildrenBinding
import com.janus.aprendiendonumeros.domain.auth.AuthImpl
import com.janus.aprendiendonumeros.presentation.AuthViewModel
import com.janus.aprendiendonumeros.presentation.AuthViewModelFactory
import com.janus.aprendiendonumeros.ui.base.BaseFragment
import com.janus.aprendiendonumeros.ui.dialog.InformationDialog
import com.janus.aprendiendonumeros.ui.dialog.LoadingDialog
import com.janus.aprendiendonumeros.ui.utilities.Constant
import com.janus.aprendiendonumeros.ui.utilities.UIAnimations
import com.janus.aprendiendonumeros.ui.utilities.goTo

class SignInChildrenFragment : BaseFragment(R.layout.fragment_sign_in_children) {

    private lateinit var binding: FragmentSignInChildrenBinding
    private val listPassword: MutableList<Int> = mutableListOf()
    private val anim: UIAnimations by lazy { UIAnimations(requireContext()) }
    private val loadingDialog: LoadingDialog by lazy { LoadingDialog(requireActivity()) }
    private val authViewModel by viewModels<AuthViewModel> {
        AuthViewModelFactory(
            AuthImpl(
                AuthDataSource()
            )
        )
    }

    override fun initUI(view: View) {
        binding = FragmentSignInChildrenBinding.bind(view)

        addEvents()
    }

    private fun addEvents() {
        binding.btnSignIn.setOnClickListener { validateCredentials() }
        addEventImagesPassword()
    }

    private fun addEventImagesPassword() {
        val allImagesPassword =
            binding.containerImagesPassword.gridImagesPassword.children.iterator()
        allImagesPassword.forEach { view ->
            view.setOnClickListener { selectImageForPassword((it as ImageView)) }
        }
    }

    private fun validateCredentials() {
        var password = ""
        listPassword.forEach { password += (it * 9876543).toString() }
        when {
            password.isEmpty() -> messagePasswordEmpty()
            else -> signIn(password)
        }
    }

    private fun signIn(passwordAdult: String) {
        val passwordEncrypt: String = AESCrypt.encrypt(passwordAdult)
        authViewModel.signInChild(passwordEncrypt).observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> loadingDialog.startDialog(TextProvider.alertDialogs["login_failed_title"]
                    ?: "Comprobando la contraseña...")
                is Resource.Success -> resultSuccess(result.data)
                is Resource.Failure -> resultFailure()
            }
        })
    }

    private fun resultSuccess(idUser: String) {
        loadingDialog.dismiss()
        val action = SignInFragmentDirections.actionSignInToMenu(idUser)
        goTo(action)
    }

    private fun resultFailure() {
        loadingDialog.dismiss()
        mContext.showDialogInformation(
            icon = InformationDialog.ICON_ERROR,
            title = TextProvider.alertDialogs["login_failed_title"] ?: "¡Lo sentimos!",
            text = TextProvider.alertDialogs["login_failed_body"]
                ?: "Ningun usuario corresponde a los datos ingresados."
        )
    }

//    private fun selectImageForPassword(view: ImageView) {
//        val tag: String = view.tag.toString()
//        if (tag == Constant.STATUS_DESELECTED) {
//            if (listPassword.size < Constant.USER_PASSWORD_CHILDREN_LENGTH) {
//                view.selectView(Constant.STATUS_SELECTED, R.drawable.ic_mark_selection, 0)
//                val index: Int =
//                    binding.containerImagesPassword.gridImagesPassword.indexOfChild(view) + 1
//                listPassword[index] = view
//                addImagesToContainer(listPassword.values.toList())
//            } else
//                listPassword.values.forEach { anim.startAnimation(it, R.anim.attention) }
//        } else {
//            val padding = resources.getDimension(R.dimen.separation_extra_small).toInt()
//            view.selectView(Constant.STATUS_DESELECTED, 0, padding)
//            listPassword.remove(binding.containerImagesPassword.gridImagesPassword.indexOfChild(view) + 1)
//            addImagesToContainer(listPassword.values.toList())
//        }
//    }

    private fun selectImageForPassword(view: ImageView) {
        if (listPassword.size < Constant.USER_PASSWORD_CHILDREN_LENGTH) {
            val index: Int =
                binding.containerImagesPassword.gridImagesPassword.indexOfChild(view) + 1
            listPassword.add(index)
            addImagesToContainer(view)
        } else {
            anim.startAnimation(binding.containerImagesPassword.previewPassword, R.anim.attention)
        }
    }


//    private fun ImageView.selectView(tag: String, resDrawable: Int, padding: Int) {
//        this.tag = tag
//        this.setBackgroundResource(resDrawable)
//        this.setPadding(padding, padding, padding, padding)
//    }

    private fun addImagesToContainer(view: ImageView) {
        val padding = resources.getDimension(R.dimen.separation_extra_small).toInt()

        val previewView: ImageView = ImageView(context).apply {
            val size: Int = resources.getDimension(R.dimen.height_button_small).toInt()
            this.layoutParams = ViewGroup.LayoutParams(size, size)
            this.adjustViewBounds = true
            this.setImageDrawable(view.drawable)
            this.alpha = 0.7F
            this.setPadding(padding, padding, padding, padding)
        }
        binding.containerImagesPassword.previewPassword.addView(previewView)
    }

    private fun messagePasswordEmpty() {
        mContext.showDialogInformation(
            icon = InformationDialog.ICON_WARNING,
            title = "¡Lo sentimos!",
            text = "No se puede iniciar sesión si no se ingresa una contraseña." +
                    "\n\nPor favor elige 3 imagenes para tu contraseña."
        )
    }
}