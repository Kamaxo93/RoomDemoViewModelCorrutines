package com.example.roomdemo

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.roomdemo.databinding.ActivityMainBinding
import com.example.roomdemo.db.Subscriber
import com.example.roomdemo.db.SubscriberDataBase
import com.example.roomdemo.db.SubscriberRepository

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    private var viewModel: SubscriberViewModel? = null
    private var adapter: SubscribeAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        val dao = SubscriberDataBase.getInstance(application).subscriberDAO
        val repository = SubscriberRepository(dao)
        val factory = SubscribeViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[SubscriberViewModel::class.java]
        initializeListener()
        initializeView()
        display()
        viewModel?.message?.observe(this, Observer { event ->
            event.getContentIfNotHandled()?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun initializeView() {
        binding?.apply {
            viewModel?.saveOrUpdateBtnLabel?.observe(this@MainActivity, Observer {
                mainActivityBtnSaveSubscriber.text = it
            })
            viewModel?.clearAllOrDeleteBtnLabel?.observe(this@MainActivity, Observer {
                mainActivityBtnDeleteSubscriber.text = it
            })
            adapter = SubscribeAdapter { selectedItem: Subscriber ->
                listItemClicked(selectedItem)
            }
            mainActivityListSubscriber.adapter = adapter
        }
    }

    private fun initializeListener() {
        binding?.apply {
            mainActivityBtnSaveSubscriber.setOnClickListener {
                if (mainActivityInputNameSubscriber.text.toString().isEmpty().not() &&
                    mainActivityInputEmailSubscriber.text.toString().isEmpty().not()
                ) {
                    viewModel?.saveOrUpdate(
                        mainActivityInputNameSubscriber.text.toString(),
                        mainActivityInputEmailSubscriber.text.toString()
                    )
                    mainActivityInputNameSubscriber.text.clear()
                    mainActivityInputEmailSubscriber.text.clear()

                } else {
                    Toast.makeText(
                        applicationContext,
                        "Falta un campo por rellennar",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            mainActivityBtnDeleteSubscriber.setOnClickListener { viewModel?.clearAllOrDelete() }
        }
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun display() {
        viewModel?.subscribers?.observe(this, Observer {
            Log.i("MyTag", it.toString())
            adapter?.setList(it)
            adapter?.notifyDataSetChanged()
        })

    }

    private fun listItemClicked(subscriber: Subscriber) {
//        Toast.makeText(this, "el subcristor selecionado es: ${subscriber.name}", Toast.LENGTH_SHORT).show()
        binding?.apply {
            mainActivityInputNameSubscriber.setText(subscriber.name)
            mainActivityInputEmailSubscriber.setText(subscriber.email)
        }
        viewModel?.initUpdateAndDelete(subscriber)
    }

}