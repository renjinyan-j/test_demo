package org.example.finalproject.demos.pojo;

import java.util.List;

public class ProductWithImg extends Products {
   private List<Images> images;
   public List<Images> getImages() {
       return images;
   }
   public void setImages(List<Images> images) {
       this.images = images;
   }
}
