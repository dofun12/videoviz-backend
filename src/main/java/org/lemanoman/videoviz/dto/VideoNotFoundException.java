/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.lemanoman.videoviz.dto;

/**
 *
 * @author Kevim Such
 */
public class VideoNotFoundException extends Exception{

    public VideoNotFoundException(String message) {
        super(message);
    }
}
