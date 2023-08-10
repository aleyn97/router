package com.aleyn.router.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.aleyn.router.LRouter
import com.aleyn.router.util.dLog

/**
 * @author: Aleyn
 * @date: 2023/6/9 11:00
 */

class RouterContentProvider : ContentProvider() {

    override fun onCreate(): Boolean {
        if (LRouter.enabledAutoInit) {
            "Router Auto Init".dLog()
            LRouter.init(context)
        }
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? = null

    override fun getType(uri: Uri): String? = null

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?) = 0

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ) = 0

}