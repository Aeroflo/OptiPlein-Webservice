package PointDeVente;

import lombok.Builder;

public class PointDeVente {

        String id;
        Double latitude;
        Double longitude;

        String address;
        String ville;

        public static class PointDeVenteBuilder{
                String id;
                Double latitude;
                Double longitude;

                String address;
                String ville;

                public PointDeVenteBuilder(String id, Double latitude, Double longitude){
                        this.id = id;
                        this.longitude = longitude;
                        this.latitude = latitude;
                }
                public PointDeVenteBuilder address(String address){this.address = address; return this;}
                public PointDeVenteBuilder ville(String ville){this.ville = ville; return this;}



                public PointDeVente build(){
                        return new PointDeVente(this);
                }
        }

        public PointDeVente(PointDeVenteBuilder b){
                this.address = b.address;
                this.ville = b.ville;
        }
}
