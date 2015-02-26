
package cn.zmdx.kaka.locker.notify.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import android.text.TextUtils;
import cn.zmdx.kaka.locker.notify.filter.NotifyFilterManager.NotifyFilterEntity;

public class ListCompare {

    ArrayList<SortEntry> sortList = new ArrayList<SortEntry>();

    HashMap<String, Integer> alphaIndexer = new HashMap<String, Integer>();;

    public String[] sections;

    public class SortEntry implements Comparable<SortEntry> {
        private String mText = "";

        public ArrayList<String> mPinyin = null;

        public int order;

        public SortEntry(String title, int order) {
            if (title != null) {
                mText = title;
            }

            this.order = order;
        }

        public String getText() {
            return mText;
        }

        public int getOrder() {
            return this.order;
        }

        public void setTextPy(String sPy) {
            String[] pyList = sPy.split(" ");

            if (mPinyin == null) {
                mPinyin = new ArrayList<String>();
            }

            for (int i = 0; i < pyList.length; i++) {
                mPinyin.add(pyList[i]);
            }
        }

        public int getPyCount() {
            if (mPinyin == null) {
                return 0;
            }

            return mPinyin.size();
        }

        public ArrayList<String> getAllPy() {
            return mPinyin;
        }

        public String getCharPy(int index) {
            if (mPinyin == null || index >= mPinyin.size()) {
                return null;
            }

            return mPinyin.get(index);
        }

        public void appendPy(String sPy) {
            if (mPinyin == null) {
                mPinyin = new ArrayList<String>();
            }

            mPinyin.add(sPy);
        }

        public void setText(String text) {
            mText = text;
        }

        @Override
        public int compareTo(SortEntry another) {
            if (this.mText != null)
                return this.mText.compareTo(another.getText());
            else
                throw new IllegalArgumentException();
        }
    }

    private void addSortEntryToList(SortEntry item, List<SortEntry> list, int i) {
        int nIndex1 = 0;
        int nIndex2 = list.size() - 1;
        int nIndex = 0;

        if (item == null) {
            return;
        }

        String text = item.getText();

        if (TextUtils.isEmpty(text)) {
            item.appendPy("");
            list.add(item);
            return;
        }

        if (nIndex2 < 0) {
            String firtChar = item.getText().substring(0, 1);
            item.appendPy(getPinyin(firtChar, true));
            list.add(item);
            return;
        }

        while (true) {
            nIndex = (nIndex2 + nIndex1) / 2;
            try {
                SortEntry temp = list.get(nIndex);

                int result = mSortWay.compare(item, temp);
                if (result > 0) {
                    nIndex1 = nIndex + 1;
                } else if (result < 0) {
                    nIndex2 = nIndex - 1;
                } else {
                    list.add(nIndex, item);
                    break;
                }

                if (nIndex1 > nIndex2) {
                    if (result > 0) {
                        if (nIndex >= list.size() - 1) {
                            list.add(item);
                        } else {
                            list.add(nIndex + 1, item);
                        }
                    } else {
                        list.add(nIndex, item);

                    }

                    break;
                }

            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    Comparator<SortEntry> mSortWay = new Comparator<SortEntry>() {

        @Override
        public int compare(SortEntry lhs, SortEntry rhs) {
            int i = 0;
            String sChar1 = null;
            String sChar2 = null;

            while (true) {
                String sPy1 = lhs.getCharPy(i);
                String sPy2 = rhs.getCharPy(i);

                if (sPy1 == null && sPy2 == null) {
                    if (i < lhs.getText().length()) {
                        sChar1 = lhs.getText().substring(i, i + 1);
                    } else {
                        return -1;
                    }

                    if (i < rhs.getText().length()) {
                        sChar2 = rhs.getText().substring(i, i + 1);
                    } else {
                        return 1;
                    }

                    if (sChar2.equalsIgnoreCase(sChar1)) {
                        String sPy = getPinyin(sChar1, i == 0 ? true : false);
                        lhs.appendPy(sPy);
                        rhs.appendPy(sPy);
                        continue;
                    } else {
                        lhs.appendPy(getPinyin(sChar1, i == 0 ? true : false));
                        rhs.appendPy(getPinyin(sChar2, i == 0 ? true : false));
                        continue;
                    }
                } else if (sPy1 == null) {
                    if (i < lhs.getText().length()) {
                        sChar1 = lhs.getText().substring(i, i + 1);
                    } else {
                        return -1;
                    }

                    sChar2 = rhs.getText().substring(i, i + 1);
                    if (sChar2.equalsIgnoreCase(sChar1)) {
                        lhs.appendPy(rhs.getCharPy(i));
                        continue;
                    } else {
                        lhs.appendPy(getPinyin(sChar1, i == 0 ? true : false));
                        continue;
                    }
                } else if (sPy2 == null) {
                    if (i < rhs.getText().length()) {
                        sChar2 = rhs.getText().substring(i, i + 1);
                    } else {
                        return 1;
                    }

                    sChar1 = lhs.getText().substring(i, i + 1);
                    if (sChar1.equalsIgnoreCase(sChar2)) {
                        rhs.appendPy(lhs.getCharPy(i));
                        continue;
                    } else {
                        rhs.appendPy(getPinyin(sChar2, i == 0 ? true : false));
                        continue;
                    }
                } else {
                    int nRet = sPy1.compareTo(sPy2);
                    if (nRet == 0) {
                        i += 1;
                        continue;
                    }

                    return nRet;
                }

            }
        }
    };

    public ArrayList<SortEntry> initSortEntry(ArrayList<NotifyFilterEntity> list) {
        sortList.clear();
        for (int i = 0; i < list.size(); i++) {
            String appName = list.get(i).getNotifyUSName();
            SortEntry sortEntry = new SortEntry(appName, i);
            addSortEntryToList(sortEntry, sortList, i);
        }
        updateAlphaIndexerAndSections();
        return sortList;
    }

    private void updateAlphaIndexerAndSections() {
        if (sortList != null && sortList.size() > 0) {
            int size = sortList.size();

            for (int i = 0; i < size; i++) {
                String sortKey = sortList.get(i).getCharPy(0);
                String alpha = getAlpha(sortKey, true);
                if (!alphaIndexer.containsKey(alpha)) {
                    alphaIndexer.put(alpha, i);
                }
            }

            Set<String> sectionLetters = alphaIndexer.keySet();
            ArrayList<String> sectionList = new ArrayList<String>(sectionLetters);
            Collections.sort(sectionList);
            sections = new String[sectionList.size()];
            sectionList.toArray(sections);
        }
    }

    public HashMap<String, Integer> getAlphaIndexer() {
        return alphaIndexer;
    }

    public String[] getSections() {
        return sections;
    }

    private String getPinyin(String source, boolean addHeader) {
        return getSortString(source, addHeader);
    }

    private String getSortString(String srcString, boolean addHeader) {
        int style = NameStyle.getIntance().guessNameStyle(srcString);
        if (style == NameStyle.UNDEFINED || style == NameStyle.CJK) {
            style = NameStyle.getIntance().getAdjustedNameStyle(style);
        }
        String translateString = LocaleUtils.getIntance().getSortKey(srcString, style);
        if (addHeader) {
            String alphaString = getAlpha(translateString, false);

            if ((alphaString == null) || (alphaString.compareToIgnoreCase("#") == 0)) {
                alphaString = "{";
            }

            return alphaString + translateString;
        }

        return translateString;
    }

    private String getAlpha(String str, boolean matched) {
        if (str == null) {
            if (!matched) {
                return "#";
            } else {
                return "{";
            }
        }

        if (str.trim().length() == 0) {
            if (!matched) {
                return "#";
            } else {
                return "{";
            }
        }

        char c = str.trim().substring(0, 1).charAt(0);

        Pattern pattern = Pattern.compile("^[A-Za-z]+$");
        if (pattern.matcher(c + "").matches()) {
            return (c + "").toUpperCase();
        } else {
            if (!matched) {
                return "#";
            } else {
                return "{";
            }
        }
    }

}
