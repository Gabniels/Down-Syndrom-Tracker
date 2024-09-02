package net.skripsi.downsyndromtracker.information

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import coil.load
import com.smarteist.autoimageslider.SliderViewAdapter
import net.skripsi.downsyndromtracker.R
import net.skripsi.downsyndromtracker.databinding.ItemSliderBinding
import net.skripsi.downsyndromtracker.model.SliderModel

class SliderAdapter(context: Context): SliderViewAdapter<SliderAdapter.ViewHolder>() {

    private var mSliderItems: MutableList<SliderModel> = ArrayList()

    fun renewItems(sliderItems: List<SliderModel>) {
        this.mSliderItems = sliderItems.toMutableList()
        notifyDataSetChanged()
    }

    fun deleteItem(position: Int) {
        this.mSliderItems.removeAt(position)
        notifyDataSetChanged()
    }

    fun addItem(sliderItem: SliderModel) {
        this.mSliderItems.add(sliderItem)
        notifyDataSetChanged()
    }

    class ViewHolder(view: View): SliderViewAdapter.ViewHolder(view) {
        private val binding = ItemSliderBinding.bind(view)

        fun bindData(item : SliderModel) {
            binding.imgSlider.load(item.imageUrl)
        }
    }

    override fun getCount(): Int {
       return mSliderItems.size
    }

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_slider, parent, false)
        )
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bindData(mSliderItems[position])
    }


}