package com.digi.common.adapter.repository.impl;

import com.digi.common.adapter.repository.UserQuickLinkRepository;
import com.digi.common.entity.UserQuickLink;
import com.digi.common.repository.JPAUserQuickLinkRepository;
import com.digi.common.view.QuickLinkView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserQuickLinkRepositoryImpl implements UserQuickLinkRepository {

	@Autowired
	private JPAUserQuickLinkRepository jpaUserQuickLinkRepository;

	@Override
	public List<QuickLinkView> findListByUserName(String userName) {

		List<QuickLinkView> quickLinkLst = jpaUserQuickLinkRepository.findListByUserName(userName);
		return quickLinkLst;
	}

	@Override
	public void saveAll(List<UserQuickLink> valuesToInsert) {
		jpaUserQuickLinkRepository.saveAll(valuesToInsert);
	}

	@Override
	public void deleteAll(List<String> prodDelLst, List<String> subProdDelLst,String userName) {

		jpaUserQuickLinkRepository.deleteData(prodDelLst,subProdDelLst,userName);
	}
}
