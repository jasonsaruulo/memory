package com.shafiq.saruul.memory.main

import android.os.Bundle
import com.shafiq.saruul.memory.R
import dagger.android.AndroidInjection
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var presenter: MainContract.Presenter
    @Inject
    lateinit var fragment: MainFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var mainFragment = supportFragmentManager.findFragmentById(R.id.main_content)
                as? MainFragment
        if (mainFragment == null) {
            supportFragmentManager.beginTransaction().add(R.id.main_content, fragment).commit()
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        presenter.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onBackPressed() {
        if (presenter.onBackPressed()) {
            super.onBackPressed()
        }
    }
}
