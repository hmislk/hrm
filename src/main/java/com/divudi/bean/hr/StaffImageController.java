/*
 * MSc(Biomedical Informatics) Project
 *
 * Development and Implementation of a Web-based Combined Data Repository of
 Genealogical, Clinical, Laboratory and Genetic Data
 * and
 * a Set of Related Tools
 */
package com.divudi.bean.hr;

import com.divudi.bean.common.UtilityController;
import com.divudi.entity.Staff;
import com.divudi.facade.StaffFacade;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.io.IOUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.file.UploadedFile;

/**
 *
 * @author Dr. M. H. B. Ariyaratne, MBBS, PGIM Trainee for MSc(Biomedical
 * Informatics)
 */
@Named
@RequestScoped
public class StaffImageController implements Serializable {

    StreamedContent scCircular;
    StreamedContent scCircularById;
    private UploadedFile file;
    @EJB
    StaffFacade staffFacade;

    private static final long serialVersionUID = 1L;

    public StaffFacade getStaffFacade() {
        return staffFacade;
    }

    public void setStaffFacade(StaffFacade staffFacade) {
        this.staffFacade = staffFacade;
    }

    @Inject
    StaffController staffController;

    public StaffController getStaffController() {
        return staffController;
    }

    public void setStaffController(StaffController staffController) {
        this.staffController = staffController;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public String saveSignature() {
        InputStream in;
        if (file == null || "".equals(file.getFileName())) {
            return "";
        }
        if (file == null) {
            UtilityController.addErrorMessage("Please select an image");
            return "";
        }
        if (getStaffController().getCurrent().getId() == null || getStaffController().getCurrent().getId() == 0) {
            UtilityController.addErrorMessage("Please select staff member");
            return "";
        }
        ////System.out.println("file name is not null");
        ////System.out.println(file.getFileName());
        try {
            in = getFile().getInputStream();
            File f = new File(getStaffController().getCurrent().toString() + getStaffController().getCurrent().getFileType());
            FileOutputStream out = new FileOutputStream(f);

            //            OutputStream out = new FileOutputStream(new File(fileName));
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = in.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            in.close();
            out.flush();
            out.close();

            getStaffController().getCurrent().setRetireComments(f.getAbsolutePath());
            getStaffController().getCurrent().setFileName(file.getFileName());
            getStaffController().getCurrent().setFileType(file.getContentType());
            in = file.getInputStream();
            getStaffController().getCurrent().setBaImage(IOUtils.toByteArray(in));
            getStaffFacade().edit(getStaffController().getCurrent());
            return "";
        } catch (IOException e) {
            ////System.out.println("Error " + e.getMessage());
            return "";
        }

    }

    public void removeSignature() {
        getStaffController().getCurrent().setRetireComments(null);
        getStaffController().getCurrent().setFileName(null);
        getStaffController().getCurrent().setFileType(null);
        getStaffController().getCurrent().setBaImage(null);
        getStaffFacade().edit(getStaffController().getCurrent());
    }

    public StreamedContent displaySignature(Long stfId) {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context.isPostback() || stfId == null) {
            return null;
        }

        Staff tempStaff = staffFacade.findFirstBySQL("select s from Staff s where s.baImage != null and s.id = " + stfId);

        if (tempStaff == null) {
            return null;
        } else if (tempStaff.getId() != null && tempStaff.getBaImage() != null) {
            return DefaultStreamedContent.builder()
                    .stream(() -> new ByteArrayInputStream(tempStaff.getBaImage()))
                    .contentType(tempStaff.getFileType())
                    .name(tempStaff.getFileName())
                    .build();
        } else {
            return null;
        }
    }

   

}
