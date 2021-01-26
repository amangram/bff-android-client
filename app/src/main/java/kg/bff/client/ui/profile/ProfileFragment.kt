package kg.bff.client.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import kg.bff.client.*
import kg.bff.client.data.model.State
import kg.bff.client.ui.GlideApp
import kg.bff.client.ui.auth.AuthActivity
import kg.bff.client.ui.stadium.detail.StadiumDetailActivity
import kg.bff.client.ui.stadium.list.StadiumsAdapter
import kotlinx.android.synthetic.main.fragment_profile.*
import org.koin.android.viewmodel.ext.android.viewModel

class ProfileFragment : Fragment() {

    private val myViewModel: ProfileViewModel by viewModel()
    private val auth by lazy { FirebaseAuth.getInstance() }
    private lateinit var id: String
    private lateinit var stadiumsAdapter: StadiumsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth.currentUser?.uid?.let { uid ->
            id = uid
        }
        setAdapter()
        setData()
        getSavedStadiums()
    }

    private fun getSavedStadiums() {
        myViewModel.getSavedStadiums(id).observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is State.Loading -> pb_profile.visible()
                is State.Success -> {
                    pb_profile.gone()
                    Log.d("ffff", "getSavedStadiums: ${state.data.size}")
                    stadiumsAdapter.swapData(state.data)
                }
                is State.Failed -> {
                    pb_profile.gone()
                    activity?.toast(getString(R.string.error))
                }
            }
        })
    }

    private fun setAdapter() {
        stadiumsAdapter = StadiumsAdapter({ stadium ->
            startActivity(
                Intent(context, StadiumDetailActivity::class.java)
                    .putExtra("stadiumId", stadium.id)
            )
        }, { stadium ->
            val list = stadium.saved.toMutableList()
            if (stadium.saved.contains(id)) {
                list.remove(id)
                activity?.toast("not")
            } else {
                list.add(id)
                activity?.toast("saving")
            }
            myViewModel.save(stadium.id, list).observe(viewLifecycleOwner, Observer { state ->
                stateListener(state)
            })
        })
        rv_saved_stadiums.apply {
            adapter = stadiumsAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun stateListener(state: State<String>) {
        when (state) {
            is State.Success -> {

            }
            is State.Failed -> {
                activity?.toast(getString(R.string.error))
            }
        }
    }

    private fun setData() {
        myViewModel.user.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is State.Loading -> {
                    pb_profile.visible()
                }
                is State.Success -> {
                    pb_profile.gone()
                    context?.toast(R.string.error.toString())
                    state.data.let { user ->
                        tv_user_name.text = user.name
                        tv_user_phone.text = user.phone
                        tv_user_birthdate.text = user.birthdate
                        GlideApp.with(this)
                            .load(user.image?.preview)
                            .centerCrop()
                            .placeholder(R.drawable.ic_account_circle_black_24dp)
                            .into(iv_profile_image)
                    }
                }
                is State.Failed -> {
                    context?.toast(R.string.error.toString())
                }
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.edit_profile -> {
                findNavController().navigate(R.id.action_navigation_profile_to_editProfileFragment)
                return true
            }
            R.id.log_out -> {
                auth.signOut()
                activity?.let {
                    it.changeActivity<AuthActivity>()
                    it.finish()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}