package com.kan.salads.activity.home

import com.kan.salads.ShoppingCartItemViewModel


interface HomeView {
    fun setShoppingCartItems(items: List<ShoppingCartItemViewModel>)
    fun setSelectedCartCount(selectedCount:Int)
    fun setIsLoggedInAnonymously()
    fun setIsLoggedInAsUser(userName:String)
    fun showLoginActivity()
    fun showNotImplemented()
}

