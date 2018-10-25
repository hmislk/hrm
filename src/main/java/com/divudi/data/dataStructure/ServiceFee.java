/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.divudi.data.dataStructure;

import com.divudi.entity.Item;
import com.divudi.entity.ItemFee;
import com.divudi.entity.Service;
import com.divudi.entity.inward.TimedItemFee;
import java.util.List;

/**
 *
 * @author safrin
 */
public class ServiceFee {

    private Service service;
    private Item item;
    private List<ItemFee> itemFees;
    private List<TimedItemFee> timedItemFees;

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public List<ItemFee> getItemFees() {
        return itemFees;
    }

    public void setItemFees(List<ItemFee> itemFees) {
        this.itemFees = itemFees;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public List<TimedItemFee> getTimedItemFees() {
        return timedItemFees;
    }

    public void setTimedItemFees(List<TimedItemFee> timedItemFees) {
        this.timedItemFees = timedItemFees;
    }

}
