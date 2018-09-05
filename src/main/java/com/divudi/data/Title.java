/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.divudi.data;

/**
 *
 * @author Dr. M H B Ariyaratne <buddhika.ari at gmail.com>
 */
public enum Title {

    Mr,
    Mrs,
    Miss,
    Ms,
    Master,
    Baby,
    Rev,
    RtRev,
    Hon,
    RtHon,
    Dr,
    DrMrs,
    DrMs,
    DrMiss,
    Prof,
    ProfMrs,
    Other,
    Baby_Of;

    public String getLabel() {
        switch (this) {
            case Baby_Of:
                return "BABY OF ";
            case DrMiss:
                return "DR. (MISS) ";
            case DrMrs:
                return "DR. (MRS) ";
            case DrMs:
                return "DR. (MS) ";
            case Hon:
                return "HON. ";
            case Dr:
                return "DR. ";
            case Mr:
                return "MR. ";
            case Miss:
                return "MISS. ";
            case Mrs:
                return "MRS. ";
            case Ms:
                return "MS. ";
            case Prof:
                return "PROF. ";
            case ProfMrs:
                return "PROF. (MRS) ";
            case Rev:
                return "REV. ";
            case RtHon:
                return "RT. HON. ";
            case RtRev:
                return "RT. REV. ";
            case Other:
                return "OTHER";
            default:
                return this.toString();

        }
    }
}
