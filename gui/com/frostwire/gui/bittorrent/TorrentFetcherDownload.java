package com.frostwire.gui.bittorrent;

import java.awt.event.ActionListener;
import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import org.gudy.azureus2.core3.download.DownloadManager;
import org.gudy.azureus2.core3.torrent.TOTorrent;
import org.gudy.azureus2.core3.torrent.TOTorrentException;
import org.gudy.azureus2.core3.torrentdownloader.TorrentDownloader;
import org.gudy.azureus2.core3.torrentdownloader.TorrentDownloaderCallBackInterface;
import org.gudy.azureus2.core3.torrentdownloader.TorrentDownloaderFactory;
import org.gudy.azureus2.core3.util.TorrentUtils;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.I18n;
import com.limegroup.gnutella.settings.SharingSettings;

public class TorrentFetcherDownload implements BTDownload {

    private static final String STATE_DOWNLOADING = I18n.tr("Downloading Torrent");
    private static final String STATE_ERROR = I18n.tr("Error");
    private static final String STATE_CANCELED = I18n.tr("Canceled");

    private String _uri;
    private final String _torrentSaveDir;
    private TorrentDownloader _torrentDownloader;
    private final String _displayName;
    private final String _hash;
    private final long _size;
    private final boolean _partialDownload;
    private final ActionListener _postPartialDownloadAction;

    private String _state;
    private boolean _removed;
    private BTDownload _delegate;
	private String relativePath;

    public TorrentFetcherDownload(String uri, String displayName, String hash, long size, boolean partialDownload, ActionListener postPartialDownloadAction) {
        _uri = uri;
        _torrentSaveDir = SharingSettings.TORRENTS_DIR_SETTING.getValue().getAbsolutePath();
        _torrentDownloader = TorrentDownloaderFactory.create(new TorrentDownloaderListener(), _uri, null, _torrentSaveDir);
        _displayName = displayName;
        _hash = hash;
        _size = size;
        _partialDownload = partialDownload;
        _postPartialDownloadAction = postPartialDownloadAction;

        _state = STATE_DOWNLOADING;
        _torrentDownloader.start();
    }

    public TorrentFetcherDownload(String uri, boolean partialDownload, ActionListener postPartialDownloadAction) {
        this(uri, uri, "", -1, partialDownload, postPartialDownloadAction);
    }

    public TorrentFetcherDownload(String uri, String relativePath,
			ActionListener postPartialDownloadAction) {
    	this(uri, uri, "", -1, true, postPartialDownloadAction);
    	this.relativePath = relativePath;
	}

	public long getSize() {
        return _delegate != null ? _delegate.getSize() : _size;
    }

    public String getDisplayName() {
        return _delegate != null ? _delegate.getDisplayName() : _displayName;
    }

    public boolean isResumable() {
        return _delegate != null ? _delegate.isResumable() : false;
    }

    public boolean isPausable() {
        return _delegate != null ? _delegate.isPausable() : false;
    }

    public boolean isCompleted() {
        return _delegate != null ? _delegate.isCompleted() : false;
    }

    public int getState() {
        return _delegate != null ? _delegate.getState() : -1;
    }

    public void remove() {
        if (_delegate != null) {
            _delegate.remove();
        } else {
            _removed = true;
            _torrentDownloader.cancel();
            _torrentDownloader.getFile().delete();
        }
    }

    public void pause() {
        if (_delegate != null) {
            _delegate.pause();
        }
    }

    public void resume() {
        if (_delegate != null) {
            _delegate.resume();
        }
    }

    public File getSaveLocation() {
        return _delegate != null ? _delegate.getSaveLocation() : null;
    }

    public int getProgress() {
        return _delegate != null ? _delegate.getProgress() : 0;
    }

    public String getStateString() {
        return _delegate != null ? _delegate.getStateString() : _state;
    }

    public long getBytesReceived() {
        return _delegate != null ? _delegate.getBytesReceived() : 0;
    }

    public long getBytesSent() {
        return _delegate != null ? _delegate.getBytesSent() : 0;
    }

    public double getDownloadSpeed() {
        return _delegate != null ? _delegate.getDownloadSpeed() : 0;
    }

    public double getUploadSpeed() {
        return _delegate != null ? _delegate.getUploadSpeed() : 0;
    }

    public long getETA() {
        return _delegate != null ? _delegate.getETA() : 0;
    }

    public DownloadManager getDownloadManager() {
        return _delegate != null ? _delegate.getDownloadManager() : null;
    }

    public String getPeersString() {
        return _delegate != null ? _delegate.getPeersString() : "";
    }

    public String getSeedsString() {
        return _delegate != null ? _delegate.getSeedsString() : "";
    }

    public boolean isDeleteTorrentWhenRemove() {
        return _delegate != null ? _delegate.isDeleteTorrentWhenRemove() : false;
    }

    public void setDeleteTorrentWhenRemove(boolean deleteTorrentWhenRemove) {
        if (_delegate != null) {
            _delegate.setDeleteTorrentWhenRemove(deleteTorrentWhenRemove);
        }
    }

    public boolean isDeleteDataWhenRemove() {
        return _delegate != null ? _delegate.isDeleteDataWhenRemove() : false;
    }

    public void setDeleteDataWhenRemove(boolean deleteDataWhenRemove) {
        if (_delegate != null) {
            _delegate.setDeleteDataWhenRemove(deleteDataWhenRemove);
        }
    }

    public String getHash() {
        return _delegate != null ? _delegate.getHash() : _hash;
    }

    public String getSeedToPeerRatio() {
        return _delegate != null ? _delegate.getSeedToPeerRatio() : "";
    }

    public String getShareRatio() {
        return _delegate != null ? _delegate.getShareRatio() : "";
    }

    public boolean isPartialDownload() {
        return _delegate != null ? _delegate.isPartialDownload() : _partialDownload;
    }

    private final class TorrentDownloaderListener implements TorrentDownloaderCallBackInterface {
        private boolean[] filesSelection;
        private AtomicBoolean finished = new AtomicBoolean(false);

        public void TorrentDownloaderEvent(int state, final TorrentDownloader inf) {
            if (_removed) {
                return;
            }
            if (state == TorrentDownloader.STATE_FINISHED && finished.compareAndSet(false,true)) {
                try {
                	//single file download straight from a torrent deep search.
                	if (relativePath != null) {
                		TOTorrent torrent = TorrentUtils.readFromFile(inf.getFile(), false);
                		
                        filesSelection = new boolean[torrent.getFiles().length];
                        for (int i = 0; i < filesSelection.length; i++) {
                            filesSelection[i] = torrent.getFiles()[i].getRelativePath().equals(relativePath);
                        }
	
                	}
                	else if (_partialDownload) {
                        GUIMediator.safeInvokeAndWait(new Runnable() {
                            public void run() {
                                try {

                                    PartialFilesDialog dlg = new PartialFilesDialog(GUIMediator.getAppFrame(), inf.getFile());
                                    
                                    dlg.setVisible(true);
                                    filesSelection = dlg.getFilesSelection();
                                } catch (TOTorrentException e) {
                                    e.printStackTrace();
                                    filesSelection = null;
                                }
                            }
                        });
                        if (filesSelection == null) {
                            cancelDownload();
                            return;
                        } else {
                            if (_postPartialDownloadAction != null) {
                                try {
                                    _postPartialDownloadAction.actionPerformed(null);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    BTDownloadCreator creator = new BTDownloadCreator(inf.getFile(), filesSelection);
                    _delegate = creator.createDownload();
                    
                    if (_delegate instanceof DuplicateDownload) {
                    	cancelDownload();
                    	GUIMediator.safeInvokeLater(new Runnable() {

							@Override
							public void run() {
								BTDownloadMediator.instance().selectRowByDownload(_delegate);
							}});
                    }
                    
                } catch (Exception e) {
                    _state = STATE_ERROR;
                    e.printStackTrace();
                }
            } else if (state == TorrentDownloader.STATE_ERROR) {
                if (_hash != null && _hash.trim() != "" && (_uri.toLowerCase().startsWith("http://") || _uri.toLowerCase().startsWith("https://"))) {
                    _uri = TorrentUtil.getMagnet(_hash);
                    _torrentDownloader = TorrentDownloaderFactory.create(new TorrentDownloaderListener(), _uri, null, _torrentSaveDir);
                    _state = STATE_DOWNLOADING;
                    _torrentDownloader.start();
                } else {
                    _state = STATE_ERROR;
                }
            }
        }

		public void cancelDownload() {
			_state = STATE_CANCELED;
			GUIMediator.safeInvokeLater(new Runnable() {
			    public void run() {
			        BTDownloadMediator.instance().remove(TorrentFetcherDownload.this);
			    }
			});
		}
    }

	@Override
	public long getSize(boolean update) {
		return _delegate != null ? _delegate.getSize(update) : _size;
	}

	@Override
	public void updateDownloadManager(DownloadManager downloadManager) {
		if (_delegate != null) {
			_delegate.updateDownloadManager(downloadManager);
		}
	}
}
