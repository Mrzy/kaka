/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.zmdx.kaka.locker.notify.filter;

import java.util.HashMap;

import android.widget.SectionIndexer;

/**
 * A section indexer that is configured with precomputed section titles and
 * their respective counts.
 */
public class NotifySectionIndexer implements SectionIndexer {

	private HashMap<String, Integer> mAlphaIndexer;
	private String[] mSections;

	public NotifySectionIndexer(HashMap<String, Integer> alphaIndexer, String[] sections) {
		mAlphaIndexer = alphaIndexer;
		mSections = sections;
	}
	
	public HashMap<String, Integer> getAlphaIndexer() {
		return mAlphaIndexer;
	}
	
	@Override
	public Object[] getSections() {
		return mSections;
	}

	@Override
	public int getPositionForSection(int section) {
		String letter = mSections[section];
		return mAlphaIndexer.get(letter);
	}

	@Override
	public int getSectionForPosition(int position) {
		int length = mSections.length;
		for (int i = 0; i < length; i++) {
			int start = mAlphaIndexer.get(mSections[i]);
			int end = mAlphaIndexer.get(mSections[i + 1 == length ? i : i + 1]);
			
			if ((position >= start && position < end) ||
				(start == end && position >= start)) {
				return i;
			}
		}
		return 0;
	}
}
