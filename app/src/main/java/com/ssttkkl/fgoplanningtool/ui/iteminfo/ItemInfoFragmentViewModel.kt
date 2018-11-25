package com.ssttkkl.fgoplanningtool.ui.iteminfo

import android.view.View
import androidx.databinding.ObservableArrayMap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.data.item.Item
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import com.ssttkkl.fgoplanningtool.resources.servant.Servant
import com.ssttkkl.fgoplanningtool.resources.itemdescriptor.ItemDescriptor
import com.ssttkkl.fgoplanningtool.ui.requirementlist.RequirementListEntity
import com.ssttkkl.fgoplanningtool.ui.utils.SingleLiveEvent

class ItemInfoFragmentViewModel : ViewModel() {
    val codename = MutableLiveData<String>()

    val itemDescriptor: LiveData<ItemDescriptor> = Transformations.map(codename) { codename ->
        ResourcesProvider.instance.itemDescriptors[codename]
    }

    private fun generateEntities(codename: String, getCostItems: (Servant) -> Collection<Item>): List<RequirementListEntity> {
        val list = ArrayList<RequirementListEntity>()
        ResourcesProvider.instance.servants.values.sortedBy { it.id }.forEach { servant ->
            var requirement = 0L
            getCostItems(servant).forEach { item ->
                if (item.codename == codename)
                    requirement += item.count
            }

            if (requirement > 0)
                list.add(RequirementListEntity(servantID = servant.id,
                        requirement = requirement))
        }
        return list
    }

    val ascensionItemTitle = MyApp.context.getString(R.string.ascension_iteminfo)
    val skillItemTitle = MyApp.context.getString(R.string.skill_iteminfo)
    val dressItemTitle = MyApp.context.getString(R.string.dress_iteminfo)

    val ascensionItemEntities = Transformations.map(codename) { codename ->
        generateEntities(codename) { servant ->
            val requirement = HashMap<String, Long>()
            servant.ascensionItems.forEach { itemsForCurStage ->
                itemsForCurStage.forEach { item ->
                    requirement[item.codename] = (requirement[item.codename] ?: 0) + item.count
                }
            }
            requirement.map { Item(it.key, it.value) }
        }
    }

    val skillItemEntities = Transformations.map(codename) { codename ->
        generateEntities(codename) { servant ->
            val requirement = HashMap<String, Long>()
            servant.skillItems.forEach { itemsForCurLevel ->
                itemsForCurLevel.forEach { item ->
                    requirement[item.codename] = (requirement[item.codename] ?: 0) + item.count
                }
            }
            requirement.map { Item(it.key, it.value) }
        }
    }

    val dressItemEntities = Transformations.map(codename) { codename ->
        generateEntities(codename) { servant ->
            val requirement = HashMap<String, Long>()
            servant.dress.forEach { dress ->
                dress.items.forEach { item ->
                    requirement[item.codename] = (requirement[item.codename] ?: 0) + item.count
                }
            }
            requirement.map { Item(it.key, it.value) }
        }
    }

    val showWikiMenuEvent = SingleLiveEvent<Collection<String>>()
    val gotoWikiEvent = SingleLiveEvent<String>()
    val showServantInfoEvent = SingleLiveEvent<Int>()

    fun onClickGotoWiki() {
        showWikiMenuEvent.call(itemDescriptor.value?.wikiLinks?.keys ?: return)
    }

    fun onClickWikiMenuItem(title: String): Boolean {
        gotoWikiEvent.call(itemDescriptor.value?.wikiLinks?.get(title) ?: return false)
        return true
    }

    fun onClickItem(entity: RequirementListEntity) {
        showServantInfoEvent.call(entity.servantID)
    }
}