package com.syukron.mymealdiary.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat
import com.syukron.mymealdiary.data.model.Food
import com.syukron.mymealdiary.databinding.RvFoodListBinding
import com.syukron.mymealdiary.ui.adapter.FoodListAdapter

class FoodListView(
    context: Context,
    attributeSet: AttributeSet?
) : LinearLayoutCompat(context, attributeSet) {

    private val binding: RvFoodListBinding = RvFoodListBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    fun setListData(data: List<Food>?) {
        val adapter = binding.listRecycler.adapter as FoodListAdapter
        adapter.submitList(data)
    }

    fun setButtonClickListener(buttonClickListener: OnClickListener) {
        binding.listButton.setOnClickListener(buttonClickListener)
    }

    fun setListTitle(listText: String) {
        binding.listText.text = listText
    }

    fun setAdapter(adapter: FoodListAdapter) {
        binding.listRecycler.adapter = adapter
    }
}