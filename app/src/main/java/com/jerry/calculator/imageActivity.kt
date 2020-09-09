package com.jerry.calculator

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.DownloadListener
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_image.*
import java.io.File
import java.util.*


class imageActivity : AppCompatActivity() {
    var SelectedPhotoUri: Uri? = null
    lateinit var phoneId: String
    lateinit var UserId: String
    lateinit var ref: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT)

        val linearLayoutManager = GridLayoutManager(this, 1, LinearLayoutManager.VERTICAL, false)
        recyclerViewthing.setLayoutManager(linearLayoutManager)

        phoneId = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
         ref = FirebaseDatabase.getInstance().getReference(phoneId)


        addbtn.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 0)

            uploadImageToFirebaseStorage()
/*
            val ref= FirebaseDatabase.getInstance().getReference(phoneId)

            UserId = ref.push().key.toString()*/
        }
        showdata()

    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this,CalculatorActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            SelectedPhotoUri = data.data
            Log.d("manik", SelectedPhotoUri.toString())
        }
    }
    private fun uploadImageToFirebaseStorage() {

        if (SelectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/image/$filename")


        ref.putFile(SelectedPhotoUri!!)
                .addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener {
                        Log.d("manik", it.toString())
                        saveUserToFirebaseDatabase(it.toString())
                    }
                }
    }
    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {


        UserId = ref.push().key.toString()
        val user = User(UserId, profileImageUrl)
        ref.child(UserId).setValue(user).addOnCompleteListener {
            Toast.makeText(this, "Successfully saved", Toast.LENGTH_LONG).show()

        }
    }
    private fun showdata(){


        val option = FirebaseRecyclerOptions.Builder<User>()
            .setQuery(ref, User::class.java)
            .build()

        class ItemViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {

            var imageview:ImageView = itemView!!.findViewById(R.id.imagething)
            var uid:TextView = itemView!!.findViewById(R.id.texter)

            var delething: FloatingActionButton = itemView!!.findViewById(R.id.deletething)



            var onClickedListener: ((position: Int, descr: String) -> Unit)? = null

            fun bindView(position: Int) {

                itemView.setOnClickListener{
                    onClickedListener?.invoke(position, uid.text.toString())

                    delething.setOnClickListener {
                        Log.d("manik", "working")
                        val ref1 = FirebaseDatabase.getInstance().getReference("$phoneId").child(uid.text.toString())
                        ref1.removeValue()
                    }

                }
            }
        }

        val myfirebaseRecyclerViewAdapter = object : FirebaseRecyclerAdapter<User, ItemViewHolder>(
            option
        ) {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {

                val itemView = LayoutInflater.from(this@imageActivity)
                    .inflate(R.layout.particular_item, parent, false)
                return ItemViewHolder(itemView)
            }

            override fun onBindViewHolder(item: ItemViewHolder, position: Int, model: User) {
                val itemId = getRef(position).key.toString()

                item.bindView(position)

                ref.child(itemId).addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {}

                    override fun onDataChange(p0: DataSnapshot) {
                        Picasso.get().load(model.uri).into(item.imageview)
                        item.uid.setText(model.userId)
                    }
                })
            }
        }

            myfirebaseRecyclerViewAdapter.notifyDataSetChanged()

            recyclerViewthing.adapter = myfirebaseRecyclerViewAdapter


            myfirebaseRecyclerViewAdapter.startListening()

    }

}