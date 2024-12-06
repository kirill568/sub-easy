package com.example.subeasy.ui.home

import com.example.subeasy.data.local.entities.Subscription

interface OnItemClickListener {
    fun onItemClick(subscription: Subscription)
}