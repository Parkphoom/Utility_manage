package com.wac.utility_manage.Search

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.utility_manage.R
import com.example.utility_manage.R.drawable.icons8_collapse_arrow_50px
import com.example.utility_manage.R.drawable.icons8_more_than_50px
import com.wac.utility_manage.PaymentWaterActivity
import me.samlss.broccoli.Broccoli
import me.samlss.broccoli.BroccoliGradientDrawable
import me.samlss.broccoli.PlaceholderParameter
import java.util.*


class SearchAdapter(
    recyclerView: RecyclerView?,
    context: Context,
    searchList: MutableList<SearchItem?>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {
    private var searchList: List<SearchItem?> = ArrayList()
    private var searchListFull: List<SearchItem?> = ArrayList()
    var mContext: Context
    private val visibleThreshold = 5
    private val lastVisibleItem = 0
    private val totalItemCount = 0
    private val isLoading = false
    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1

    private var broccoli: Broccoli? = Broccoli()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
//        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item,
//                parent, false);
//        return if (viewType == VIEW_TYPE_ITEM) {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_home_item, parent, false)
        return ItemViewHolder(view)
//        }
//        else {
//            val view = LayoutInflater.from(parent.context)
//                .inflate(R.layout.progressbar_dashboard, parent, false)
//            LoadingViewHolder(view)
//        }

//        return new ExampleViewHolder(v);
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {


        if (holder is ItemViewHolder) {
            populateItemRows(holder, position)
            var counter: Int = 1
            val currentItem = searchList[position]
            val icon = holder.mainContainer.findViewById<ImageView>(R.id.list_icon)
            holder.mainContainer.setOnClickListener {
                Log.d("container", "click")
                Log.d("container", counter.toString())
                Log.d("container", holder.cardContainer.childCount.toString())

                if (holder.cardContainer.childCount == counter && holder.cardContainer.childCount == 1) {
                    icon.setBackgroundResource(icons8_collapse_arrow_50px)
                    // Creating a LinearLayout.LayoutParams object for text view
                    if (currentItem != null) {
                        for (i in 0 until currentItem.finance.length()) {
                            var ispaid = false
                            var Str = ""
                            try {
                                Str = currentItem.finance.getJSONObject(i).getJSONObject("payment")
                                    .getString("status")
                                if (Str.equals("ชำระแล้ว")) {
                                    ispaid = true
                                }
                            } catch (e: Exception) {
                                Str = ""
                                ispaid = false
                            }


                            val card: RelativeLayout = RelativeLayout(mContext)

                            var params: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.MATCH_PARENT, // This will define text view width
                                RelativeLayout.LayoutParams.WRAP_CONTENT // This will define text view height
                            )
                            // Add margin to the text view
                            params.setMargins(0, 10, 10, 10)
                            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                            params.addRule(RelativeLayout.CENTER_VERTICAL)
                            Str = currentItem.finance.getJSONObject(i).getString("category")
                            val ref2 = currentItem.finance.getJSONObject(i).getString("ref2")
                            card.addView(
                                createSubView(
                                    params,
                                    counter,
                                    Str,
                                    ref2,
                                    Gravity.START,
                                    ispaid
                                )
                            )

                            var params2: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.MATCH_PARENT, // This will define text view width
                                RelativeLayout.LayoutParams.MATCH_PARENT // This will define text view height
                            )
                            params2.setMargins(10, 10, 10, 10)
                            params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                            params2.addRule(RelativeLayout.CENTER_VERTICAL)
                            try {
                                Str = currentItem.finance.getJSONObject(i).getJSONObject("payment")
                                    .getString("amount")

                            } catch (e: Exception) {
                                Str = ""
                            }

                            card.addView(
                                createSubView(
                                    params2,
                                    counter,
                                    Str,
                                    ref2,
                                    Gravity.END,
                                    ispaid
                                )
                            )

                            holder.cardContainer.addView(card, counter)

                            // Increment the counter
                            counter++
                        }
                    }

                } else {
                    icon.setBackgroundResource(icons8_more_than_50px)
                    for (i in 1 until holder.cardContainer.childCount) {
                        holder.cardContainer.removeViewAt(1)
                    }
                    counter = 1

                }

            }

        } else if (holder is LoadingViewHolder) {
            showLoadingView(holder, position)
        }

    }

    @SuppressLint("ResourceAsColor")
    private fun createSubView(
        params: RelativeLayout.LayoutParams,
        counter: Int,
        text: String,
        ref2: String,
        gravity: Int,
        ispaid: Boolean
    ): TextView {
        val text_view: TextView = TextView(mContext)
        text_view.layoutParams = params
        text_view.id = counter
        text_view.gravity = gravity
        text_view.text = text
        if (!ispaid) {
            text_view.setTextColor(Color.RED)
        }
        text_view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15F)
        text_view.setTypeface(text_view.typeface, Typeface.BOLD_ITALIC)
        text_view.typeface = Typeface.MONOSPACE
        text_view.setPadding(50, 10, 10, 10)
        text_view.setOnClickListener {
            Log.d("container", "click${text_view.id}")
            val intent = Intent(mContext, PaymentWaterActivity::class.java)
            intent.putExtra("Ref2", ref2)
            mContext.startActivity(intent)
        }

        return text_view
    }

    private fun addWhiteBorder(bmp: Bitmap, borderSize: Int): Bitmap {
        val bmpWithBorder = Bitmap.createBitmap(
            bmp.width + borderSize * 2,
            bmp.height + borderSize * 2,
            bmp.config
        )
        val canvas = Canvas(bmpWithBorder)
        canvas.drawColor(Color.BLACK)
        canvas.drawBitmap(bmp, borderSize.toFloat(), borderSize.toFloat(), null)
        return bmpWithBorder
    }

    override fun getItemViewType(position: Int): Int {
        return if (searchList[position] == null) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }

    override fun getItemCount(): Int {
        return searchList.size
    }

    override fun getFilter(): Filter {
        return exampleFilter
    }

    private val exampleFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filteredList: MutableList<SearchItem?> =
                ArrayList()
            if (constraint == null || constraint.length == 0) {
                filteredList.addAll(searchListFull)
            } else {
                val filterPattern =
                    constraint.toString().toLowerCase().trim { it <= ' ' }
                for (item in searchListFull) {
                    if (item!!.homeid.toLowerCase().contains(filterPattern) || item.category
                            .toLowerCase().contains(filterPattern)
                    ) {
                        filteredList.add(item)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(
            constraint: CharSequence,
            results: FilterResults
        ) {
            searchList.clear()
            searchList.addAll(results.values as Collection<SearchItem?>)
            notifyDataSetChanged()
        }
    }

    private inner class ItemViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var txthomeid: TextView
        var textnameVisitor: TextView
        var cardContainer: LinearLayout
        var mainContainer: RelativeLayout

        init {
            txthomeid = itemView.findViewById(R.id.searchtext_view1)
            textnameVisitor = itemView.findViewById(R.id.searchtext_view2)
            cardContainer = itemView.findViewById(R.id.card_container)
            mainContainer = itemView.findViewById(R.id.container_main)

            broccoli!!.addPlaceholder(
                PlaceholderParameter.Builder()
                    .setView(txthomeid)
                    .setDrawable(
                        BroccoliGradientDrawable(
                            Color.parseColor("#DDDDDD"),
                            Color.parseColor("#CCCCCC"), 40F, 800, LinearInterpolator()
                        )
                    )
                    .build()
            )
            broccoli!!.addPlaceholder(
                PlaceholderParameter.Builder()
                    .setView(textnameVisitor)
                    .setDrawable(
                        BroccoliGradientDrawable(
                            Color.parseColor("#DDDDDD"),
                            Color.parseColor("#CCCCCC"), 40F, 800, LinearInterpolator()
                        )
                    )
                    .build()
            )

            broccoli!!.show()
        }
    }

    private inner class LoadingViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var progressBar: ProgressBar? = null
    }

    private fun showLoadingView(viewHolder: LoadingViewHolder, position: Int) {
        //ProgressBar would be displayed
    }

    private fun populateItemRows(holder: ItemViewHolder, position: Int) {
        val currentItem = searchList[position]
        holder.txthomeid.text = currentItem!!.homeid
        holder.textnameVisitor.text = currentItem.category
        if (!holder.txthomeid.text.isNullOrEmpty()) {
            broccoli!!.removeAllPlaceholders()
        }


    }


    //    class ExampleViewHolder extends RecyclerView.ViewHolder {
    //        ImageView imagePerson;
    //        ImageView imageCar;
    //        TextView visitorTime;
    //        TextView textnameVisitor;
    //        TextView textnameContact;
    //
    //
    //        ExampleViewHolder(View itemView) {
    //            super(itemView);
    //            imagePerson = itemView.findViewById(R.id.image_person);
    //            imageCar = itemView.findViewById(R.id.image_car);
    //            visitorTime = itemView.findViewById(R.id.searchtext_view1);
    //            textnameVisitor = itemView.findViewById(R.id.searchtext_view2);
    //            textnameContact = itemView.findViewById(R.id.searchtext_view3);
    //        }
    //    }
    init {
        this.searchList = searchList
        searchListFull = ArrayList(searchList)
        mContext = context
        Log.d("searchhhh1", searchListFull.toString())
    }


}