/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.divudi.bean.common;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.Barcode39;
import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.BarcodeImageHandler;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author Buddhika
 */
@Named
@RequestScoped
public class BarcodeController {

    @Inject
    PatientController patientController;

    /**
     * Creates a new instance of BarcodeController
     */
    public BarcodeController() {
    }

    public PatientController getPatientController() {
        return patientController;
    }

    public void setPatientController(PatientController patientController) {
        this.patientController = patientController;
    }

    public StreamedContent getCreatePatientBarcode() {
        File barcodeFile = new File(getPatientController().getCurrent().toString());
        StreamedContent barcode = null;

        try {
            if (getPatientController().getCurrent() != null && getPatientController().getCurrent().getCode() != null && !getPatientController().getCurrent().getCode().trim().equals("")) {
                BarcodeImageHandler.saveJPEG(BarcodeFactory.createCode128C(getPatientController().getCurrent().getCode()), barcodeFile);
            } else {
                Barcode bc = BarcodeFactory.createCode128C("0000");
                bc.setBarHeight(5);
                bc.setBarWidth(3);
                bc.setDrawingText(true);
                BarcodeImageHandler.saveJPEG(bc, barcodeFile);
            }
            barcode = DefaultStreamedContent.builder()
                    .stream(() -> {
                        try {
                            return new FileInputStream(barcodeFile);
                        } catch (FileNotFoundException e) {
                            // Handle FileNotFoundException here
                            return null;
                        }
                    })
                    .contentType("image/jpeg")
                    .build();
        } catch (Exception ex) {
            // Handle other exceptions here
        }

        return barcode;
    }

    public StreamedContent getCreateBarcode(String code) {
    if (code == null || code.trim().equals("")) {
        return null;
    }

    File barcodeFile = new File(code);
    StreamedContent barcode = null;

    try {
        BarcodeImageHandler.saveJPEG(BarcodeFactory.createCode128C(code), barcodeFile);
        barcode = DefaultStreamedContent.builder()
                .stream(() -> {
                    try {
                        return new FileInputStream(barcodeFile);
                    } catch (FileNotFoundException e) {
                        // Optional: Log the exception or handle it as required
                        return null;
                    }
                })
                .contentType("image/jpeg")
                .build();
    } catch (Exception ex) {
        // Optional: Log the exception or handle other exceptions as required
    }

    return barcode;
}


   public StreamedContent getBarcodeBy() {
    FacesContext context = FacesContext.getCurrentInstance();
    if (context.getRenderResponse()) {
        // Rendering the view. Return a stub StreamedContent for URL generation.
        return DefaultStreamedContent.builder().build();
    } else {
        // Browser is requesting the image. Retrieve ID value from request param.
        String code = context.getExternalContext().getRequestParameterMap().get("code");
        return DefaultStreamedContent.builder()
                .stream(() -> new ByteArrayInputStream(getBarcodeBytes(code)))
                .contentType("image/jpg")
                .build();
    }
}


    public byte[] getBarcodeBytes(String code) {
        Barcode39 code39 = new Barcode39();
        code39.setCode(code);
        code39.setFont(null);
        code39.setExtended(true);
        Image image = null;
        try {
            image = Image.getInstance(code39.createAwtImage(Color.BLACK, Color.WHITE), null);
        } catch (BadElementException | IOException ex) {
            Logger.getLogger(BarcodeController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return image.getRawData();
    }

}
