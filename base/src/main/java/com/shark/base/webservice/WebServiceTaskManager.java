package com.shark.base.webservice;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class WebServiceTaskManager {
    private static WebServiceTaskManager instance;

	private static Set<WebServiceWorker<?>> workers = new HashSet<WebServiceWorker<?>>();

	public void startTask(WebServiceWorker worker, Object tag) {
		synchronized (workers) {
			worker.setTag(tag);
			workers.add(worker);
			worker.startTask();
		}
	}

	public void cancelTasks(Object tag) {
		synchronized (workers) {
			List<WebServiceWorker<?>> cancelWorkers = new ArrayList<WebServiceWorker<?>>();
			for (WebServiceWorker<?> worker : workers) {
				if (tag == worker.getTag()) {
					worker.cancelTask();
					cancelWorkers.add(worker);
				}
			}
			workers.removeAll(cancelWorkers);
		}
	}

	public void cancelAllTasks() {
		synchronized (workers) {
			for (WebServiceWorker<?> worker : workers) {
				worker.cancelTask();
			}
			workers.clear();
		}
	}

	public void removeWorker(WebServiceWorker<?> worker) {
		synchronized (workers) {
			workers.remove(worker);
		}
	}

    public static WebServiceTaskManager getInstance() {
        if(instance == null) {
            instance = new WebServiceTaskManager();
        }
        return instance;
    }
}
