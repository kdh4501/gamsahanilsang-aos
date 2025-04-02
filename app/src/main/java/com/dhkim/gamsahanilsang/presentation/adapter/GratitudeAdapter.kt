package com.dhkim.gamsahanilsang.presentation.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import com.dhkim.gamsahanilsang.domain.entity.GratitudeItem
import androidx.recyclerview.widget.RecyclerView
import com.dhkim.gamsahanilsang.R

class GratitudeAdapter(private var gratitudeList: List<GratitudeItem>, private val onItemClick: (GratitudeItem) -> Unit) :
    RecyclerView.Adapter<GratitudeAdapter.ViewHolder>(){

    // ViewHolder 클래스 정의
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.textViewGratitude)

        fun bind(item: GratitudeItem) {
            Log.d("ViewHolder", "Binding item: ${item.gratitudeText}") // 바인딩되는 데이터 확인
            textView.text = item.gratitudeText // gratitudeText를 TextView에 설정
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_gratitude, parent, false)
        return ViewHolder(view) // ViewHolder 반환
    }

    override fun getItemCount(): Int = gratitudeList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = gratitudeList[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    // 데이터 업데이트 메서드
    fun updateData(newList: List<GratitudeItem>) {
        val oldList = gratitudeList
        gratitudeList = newList // 새로운 리스트로 업데이트
        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = oldList.size

            override fun getNewListSize(): Int = newList.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition].id == newList[newItemPosition].id // ID를 비교
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition] == newList[newItemPosition] // 객체의 내용 비교
            }
        })
        diffResult.dispatchUpdatesTo(this) // 변경 사항을 RecyclerView에 알림
    }

}