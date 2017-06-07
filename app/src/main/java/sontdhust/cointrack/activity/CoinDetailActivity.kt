package sontdhust.cointrack.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import sontdhust.cointrack.R
import sontdhust.cointrack.fragment.CoinDetailTradesFragment

class CoinDetailActivity : AppCompatActivity(), CoinDetailTradesFragment.OnFragmentInteractionListener {

    private var name: String = ""
    private var adapter: SectionsPagerAdapter? = null
    private var pager: ViewPager? = null

    companion object {
        private val INTENT_NAME = "name"

        fun intent(context: Context, name: String): Intent {
            val intent = Intent(context, CoinDetailActivity::class.java)
            intent.putExtra(INTENT_NAME, name)
            return intent
        }
    }

    /*
     * Methods: AppCompatActivity
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coin_detail)
        name = intent.getStringExtra(INTENT_NAME)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.title = name
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, CoinsListActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        adapter = SectionsPagerAdapter(supportFragmentManager)
        pager = findViewById(R.id.container) as ViewPager
        pager!!.adapter = adapter
    }

    /*
     * Methods: CoinDetailTradesFragment.OnFragmentInteractionListener
     */

    override fun onFragmentInteraction(uri: Uri) {
    }

    /*
     * Helpers
     */

    private inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return CoinDetailTradesFragment.newInstance(name, position.toString())
        }

        override fun getCount(): Int {
            return 3
        }
    }
}
