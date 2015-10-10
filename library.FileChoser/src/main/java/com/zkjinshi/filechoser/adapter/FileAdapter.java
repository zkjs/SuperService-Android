package com.zkjinshi.filechoser.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.zkjinshi.filechoser.R;
import com.zkjinshi.filechoser.filter.XlsFileFilter;
import com.zkjinshi.filechoser.util.Constants;
import com.zkjinshi.filechoser.util.FileUtil;
import com.zkjinshi.filechoser.vo.FileSort;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


public class FileAdapter extends BaseAdapter implements Comparator<File> {

    private Context context;
    private LayoutInflater inflater;
    private File currentDirectory;
    private List<File> files = Collections.emptyList();
    private FileFilter fileFilter;
    
    public List<File> getFiles() {
		return files;
	}

    public FileAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.fileFilter = new XlsFileFilter();
    }

    @Override
    public int getCount() {
        return files.size();
    }

    @Override
    public File getItem(final int i) {
        return 0 <= i && i < files.size() ? files.get(i) : null;
    }

    @Override
    public long getItemId(final int i) {
        return i;
    }

    @Override
    public View getView(int i, View contentView, ViewGroup parent) {
        ViewHolder holder = null;
        if(contentView == null){
            contentView = inflater.inflate(R.layout.browser_file_list_item,null);
            holder = new ViewHolder();
            holder.textView = (TextView) contentView.findViewById(R.id.browserItemText);
            holder.imageView = (ImageView) contentView.findViewById(R.id.browserItemIcon);
            holder.info = (TextView) contentView.findViewById(R.id.browserItemInfo);
            holder.fileSize = (TextView) contentView.findViewById(R.id.browserItemfileSize);
            contentView.setTag(holder);
        }else{
            holder = (ViewHolder)contentView.getTag();
        }
        final File file = getItem(i);
        holder.textView.setText(file.getName());
        if (file.isDirectory()) {
            holder.imageView.setImageResource(R.drawable.ic_folder);
            holder.info.setText(FileUtil.getFileDate(file.lastModified()));
            holder.fileSize.setText("");
        } else {
            if(file.getName().endsWith("xls")){
                holder.imageView.setImageResource(R.drawable.ic_excel);
            }else {
                holder.imageView.setImageResource(R.drawable.ic_other);
            }
            holder.info.setText(FileUtil.getFileDate(file.lastModified()));
            holder.fileSize.setText(FileUtil.getFileSize(file.length()));
        }
        return contentView;
    }

    public void setCurrentDirectory(final File currentDirectory) {
        if (currentDirectory.getAbsolutePath().startsWith("/sys")) {
            return;
        }
        this.currentDirectory = currentDirectory;

        final File[] files = currentDirectory.listFiles(fileFilter);

        if (FileUtil.isNotEmpty(files)) {
            Arrays.sort(files, this);
        }

        setFiles(files);
    }

    private void setFiles(final File[] files) {
        final List<File> ff = FileUtil.isNotEmpty(files) ? new ArrayList<File>(Arrays.asList(files)) : new ArrayList<File>();
        this.files = ff;
        notifyDataSetChanged();
    }

    public File getCurrentDirectory() {
        return currentDirectory;
    }

    public void remove(final File file) {
        if (files.remove(file)) {
            notifyDataSetChanged();
        }
    }

    @Override
    public int compare(final File f1, final File f2) {
        if (f1.isDirectory() && f2.isFile()) {
            return -1;
        }
        if (f1.isFile() && f2.isDirectory()) {
            return 1;
        }
        FileSort sortType = Constants.FILE_SORT_TYPE;
        if(sortType == FileSort.MODIFY_TIME){
        	return sortByModifyTime(f1, f2);
        }else{
        	return sortByFileName(f1, f2);
        }
    }

    public static class ViewHolder{
        TextView textView;
        ImageView imageView;
        TextView info;
        TextView fileSize;
    }
    
    /**
     * 根据文件修改时间排序
     */
    private int sortByModifyTime(File f1,File f2){
    	return f1.lastModified()> f2.lastModified() ? -1 : 1;
    }
    
    /**
     * 根据文件名称排序
     */
    private int sortByFileName(File f1,File f2){
    	return f1.getName().compareTo(f2.getName());
    }
}
