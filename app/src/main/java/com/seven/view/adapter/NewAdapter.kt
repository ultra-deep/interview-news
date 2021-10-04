package com.seven.view.adapter

import News
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.seven.myapplication.BR
import com.seven.myapplication.databinding.RecyclerItemNewsAddvertiseBinding
import com.seven.myapplication.databinding.RecyclerItemNewsPhotoBinding
import com.seven.myapplication.databinding.RecyclerItemNewsTextBinding
import java.util.*

/**
 * @author Richi on 10/4/21.
 */
public class NewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    companion object {
        const val VIEW_TYPE_TEXT = 0;
        const val VIEW_TYPE_PHOTO = 1;
        const val VIEW_TYPE_ADVErTISE = 2;
    }

    private var  items = ArrayList<News>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            VIEW_TYPE_ADVErTISE -> {
                var binding  = RecyclerItemNewsAddvertiseBinding.inflate(LayoutInflater.from(parent.context) , parent , false)
                return NewsAdvertiseViewHolder(binding)
            }
            VIEW_TYPE_PHOTO -> {
                var binding  = RecyclerItemNewsPhotoBinding.inflate(LayoutInflater.from(parent.context) , parent , false)
                return NewsPhotoViewHolder(binding)
            }
            else -> {

                var binding  = RecyclerItemNewsTextBinding.inflate(LayoutInflater.from(parent.context) , parent , false)
                return NewsTextViewHolder(binding)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if(items.get(position).isTextType())  return VIEW_TYPE_TEXT
        if(items.get(position).isImageType())  return VIEW_TYPE_PHOTO
        return VIEW_TYPE_ADVErTISE
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        super.onBindViewHolder(holder, position, payloads)
        if(holder is BaseViewHolder) holder.binding.setVariable(BR.model , items.get(position))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    }

    fun setItems(news: Collection<News>?) {
        items.clear()
        items.addAll(news?:ArrayList())
    }

    fun getItems(): ArrayList<News> {
        return items;
    }
}

public class NewsTextViewHolder(binding: RecyclerItemNewsTextBinding) : BaseViewHolder(binding) {
}
public class NewsPhotoViewHolder(binding: RecyclerItemNewsPhotoBinding) : BaseViewHolder(binding) {
}
public class NewsAdvertiseViewHolder(binding: RecyclerItemNewsAddvertiseBinding) : BaseViewHolder(binding) {

}
public open class BaseViewHolder(var binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
}
