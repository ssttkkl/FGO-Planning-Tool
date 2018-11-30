package com.ssttkkl.fgoplanningtool.ui.servantinfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import com.ssttkkl.fgoplanningtool.resources.itemdescriptor.ItemType
import com.ssttkkl.fgoplanningtool.resources.servant.Servant
import com.ssttkkl.fgoplanningtool.ui.utils.SingleLiveEvent

class ServantInfoFragmentViewModel : ViewModel() {
    val servantID = MutableLiveData<Int>()

    val servant: LiveData<Servant> = Transformations.map(servantID) { servantID ->
        ResourcesProvider.instance.servants[servantID]
    }

    val ascensionItemTitle = MyApp.context.getString(R.string.ascension_iteminfo)
    val skillItemTitle = MyApp.context.getString(R.string.skill_iteminfo)
    val dressItemTitle = MyApp.context.getString(R.string.dress_iteminfo)

    val ascensionItemEntities: LiveData<List<ServantInfoLevelListEntity>?> = Transformations.map(servant) { servant ->
        if (servant != null && servant.id != 1)
            servant.ascensionItems.mapIndexed { idx, it ->
                ServantInfoLevelListEntity(start = idx.toString(),
                        to = (idx + 1).toString(),
                        isHorizontalArrowVisible = true,
                        items = it + Item("qp", servant.ascensionQP[idx]))
            }
        else
            null
    }

    val skillItemEntities: LiveData<List<ServantInfoLevelListEntity>?> = Transformations.map(servant) { servant ->
        if (servant != null)
            servant.skillItems.mapIndexed { idx, it ->
                ServantInfoLevelListEntity(start = (idx + 1).toString(),
                        to = (idx + 2).toString(),
                        isHorizontalArrowVisible = true,
                        items = it + Item("qp", servant.skillQP[idx]))
            }
        else
            null
    }

    val dressItemEntities: LiveData<List<ServantInfoLevelListEntity>?> = Transformations.map(servant) { servant ->
        if (servant != null)
            servant.dress.map { dress ->
                ServantInfoLevelListEntity(start = dress.localizedName,
                        to = "",
                        isHorizontalArrowVisible = false,
                        items = dress.items + Item("qp", dress.qp))
            }
        else
            null
    }

    val showWikiMenuEvent = SingleLiveEvent<Collection<String>>()
    val gotoWikiEvent = SingleLiveEvent<String>()
    val showItemInfoEvent = SingleLiveEvent<String>()

    fun onClickGotoWiki() {
        showWikiMenuEvent.call(servant.value?.wikiLinks?.keys ?: return)
    }

    fun onClickWikiMenuItem(title: String): Boolean {
        gotoWikiEvent.call(servant.value?.wikiLinks?.get(title) ?: return false)
        return true
    }

    fun onClickItem(codename: String) {
        if (ResourcesProvider.instance.itemDescriptors[codename]?.type != ItemType.General)
            showItemInfoEvent.call(codename)
    }
}