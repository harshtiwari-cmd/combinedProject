package com.digi.common.adapter.repository;

import com.digi.common.entity.UserQuickLink;
import com.digi.common.view.QuickLinkView;

import java.util.List;

public interface UserQuickLinkRepository {

	List<QuickLinkView> findListByUserName(String userName);

	void saveAll(List<UserQuickLink> valuesToInsert);

	void deleteAll(List<String> prodDelLst, List<String> subProdDelLst,String userName);
}