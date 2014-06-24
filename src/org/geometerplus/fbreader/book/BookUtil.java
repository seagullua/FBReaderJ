/*
 * Copyright (C) 2007-2014 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.fbreader.book;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.Locale;

import org.geometerplus.zlibrary.core.filesystem.*;
import org.geometerplus.zlibrary.core.image.ZLImage;
import org.geometerplus.zlibrary.ui.android.library.ZLAndroidApplication;

import org.geometerplus.fbreader.bookmodel.BookReadingException;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

public abstract class BookUtil {
	public static ZLImage getCover(Book book) {
		return book != null ? book.getCover() : null;
	}

	public static String getAnnotation(Book book) {
		try {
			return book.getPlugin().readAnnotation(book.File);
		} catch (BookReadingException e) {
			return null;
		}
	}

	private static boolean copyAsset(AssetManager assetManager,
            String fromAssetPath, String toPath) {
        InputStream in = null;
        OutputStream out = null;
        try {
          in = assetManager.open(fromAssetPath);
          new File(toPath).createNewFile();
          out = new FileOutputStream(toPath);
          copyFile(in, out);
          in.close();
          in = null;
          out.flush();
          out.close();
          out = null;
          return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
          out.write(buffer, 0, read);
        }
    }
	
	public static ZLFile getHelpFile() {
		Context context = ZLAndroidApplication.context;
		
		String asset_name = "data/book.epub";
		if(context != null) 
		{
			String data_path = context.getFilesDir().getPath(); 
			
			String full_path = data_path+"/book.epub";
			File file = new File(full_path);
		    if(!file.exists())
		    {
		    	Log.d("DEFAULT", "Copy book");
				File folder = new File(data_path);
				boolean success = true;
				if (!folder.exists()) {
				    success = folder.mkdir();
				}
				if (success) {
					if(copyAsset(context.getAssets(), asset_name, full_path)) {
						return ZLFile.createFileByPath(full_path);
					}
				}
		    }
		}
		return ZLResourceFile.createResourceFile(asset_name);
	}

	public static boolean canRemoveBookFile(Book book) {
		ZLFile file = book.File;
		if (file.getPhysicalFile() == null) {
			return false;
		}
		while (file instanceof ZLArchiveEntryFile) {
			file = file.getParent();
			if (file.children().size() != 1) {
				return false;
			}
		}
		return true;
	}

	public static UID createSHA256Uid(ZLFile file) {
		InputStream stream = null;

		try {
			final MessageDigest hash = MessageDigest.getInstance("SHA-256");
			stream = file.getInputStream();

			final byte[] buffer = new byte[2048];
			while (true) {
				final int nread = stream.read(buffer);
				if (nread == -1) {
					break;
				}
				hash.update(buffer, 0, nread);
			}

			final Formatter f = new Formatter();
			for (byte b : hash.digest()) {
				f.format("%02X", b & 0xFF);
			}
			return new UID("SHA-256", f.toString());
		} catch (IOException e) {
			return null;
		} catch (NoSuchAlgorithmException e) {
			return null;
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
				}
			}
		}
	}

}
