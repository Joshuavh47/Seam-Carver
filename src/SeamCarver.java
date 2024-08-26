public class SeamCarver {
    private int width;
    private int height;
    private Picture pictureCopy;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) {
            throw new IllegalArgumentException("argument to SeamCarver() is null\n");
        }

        pictureCopy = new Picture(picture);
        width = picture.width();
        height = picture.height();
    }

    // current picture
    public Picture picture() {
        return new Picture(pictureCopy);
    }

    // width of current picture
    public int width() {
        return width;
    }

    // height of current picture
    public int height() {
        return height;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        validateColumnIndex(x);
        validateRowIndex(y);

        // border pixels
        if (x == 0 || x == width -1 || y == 0 || y == height -1) {
            return 1000;
        }

        int up, down, left, right;
        up = pictureCopy.getRGB(x, y - 1);
        down = pictureCopy.getRGB(x, y + 1);
        left = pictureCopy.getRGB(x - 1, y);
        right = pictureCopy.getRGB(x + 1, y);
        double gradientY = gradient(up, down);
        double gradientX = gradient(left, right);

        return Math.sqrt(gradientX + gradientY);
    }

    private double gradient(int rgb1, int rgb2) {
        int r1 = (rgb1 >> 16) & 0xFF;
        int g1 = (rgb1 >>  8) & 0xFF;
        int b1 = (rgb1 >>  0) & 0xFF;
        int r2 = (rgb2 >> 16) & 0xFF;
        int g2 = (rgb2 >>  8) & 0xFF;
        int b2 = (rgb2 >>  0) & 0xFF;

        return Math.pow(r1 - r2, 2) + Math.pow(g1 - g2, 2)+ Math.pow(b1 - b2, 2);
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        double[][] energy = new double[width][height];
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                energy[col][row] = energy(col, row);
            }
        }

        //TODO: Use Dynamic Programming to find the vertical seam
        int[] vSeam = new int[height];
        double[][] distance=new double[width][height];
        int[][] edgeTo=new int[width][height];

        for(int i=0;i<distance.length;i++){
            for(int j=0;j<distance[i].length;j++){
                distance[i][j] = Double.MAX_VALUE;
            }
        }
        for(int i=0;i<distance.length;i++){
            distance[i][0]=energy(i, 0);
            edgeTo[i][0]=-1;
        }


        for(int y=1;y<height()-1;y++){
            for(int x=0;x<width()-1;x++){
                double minDist = Double.MAX_VALUE;
                int minIndex = -1;
                for(int i=-1;i<=1;i++){
                    int x1=x+i;
                    if(x1<0||x1>=width()){
                        continue;
                    }
                    if(distance[x1][y-1]<minDist){
                        minDist=distance[x1][y-1];
                        minIndex=x1;
                    }
                }
                distance[x][y]=energy(x,y)+minDist;
                edgeTo[x][y]=minIndex;
            }
        }
        double minDist = Double.POSITIVE_INFINITY;
        int minIndex = -1;

        for(int i=0;i<width();i++){
            if(distance[i][height()-1]<minDist){
                minDist=distance[i][height()-1];
                minIndex=i;
            }
        }
        vSeam[vSeam.length-1]=minIndex;
        for(int i=vSeam.length-2;i>= 0; i--) {
            vSeam[i]=edgeTo[vSeam[i+1]][i+1];
        }

        return vSeam;
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        int[] hSeam = new int[width];

        //TODO: Use Dynamic Programming to find the horizontal seam
        double[][] distance=new double[width][height];
        int[][] edgeTo=new int[width][height];

        for(int i=0;i<distance.length;i++){
            for(int j=0;j<distance[i].length;j++){
                distance[i][j] = Double.MAX_VALUE;
            }
        }
        for(int i=0;i<distance[0].length;i++){
            distance[0][i]=energy(0, i);
            edgeTo[0][i]=-1;
        }


        for(int x=1;x<width()-1;x++){
            for(int y=0;y<height()-1;y++){
                double minDist = Double.MAX_VALUE;
                int minIndex = -1;
                for(int i=-1;i<=1;i++){
                    int y1=y+i;
                    if(y1<0||y1>=width()){
                        continue;
                    }
                    if(distance[x-1][y1]<minDist){
                        minDist=distance[x-1][y1];
                        minIndex=y1;
                    }
                }
                distance[x][y]=energy(x,y)+minDist;
                edgeTo[x][y]=minIndex;
            }
        }
        double minDist = Double.POSITIVE_INFINITY;
        int minIndex = -1;

        for(int i=0;i<height();i++){
            if(distance[width()-1][i]<minDist){
                minDist=distance[width()-1][i];
                minIndex=i;
            }
        }
        hSeam[hSeam.length-1]=minIndex;
        for(int i=hSeam.length-2;i>= 0; i--) {
            hSeam[i]=edgeTo[i+1][hSeam[i+1]];
        }

        return hSeam;
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (seam == null) {
            throw new IllegalArgumentException("the argument to removeVerticalSeam() is null\n");
        }
        if (seam.length != height) {
            throw new IllegalArgumentException("the length of seam not equal height\n");
        }
        validateSeam(seam);
        if (width <= 1) {
            throw new IllegalArgumentException("the width of the picture is less than or equal to 1\n");
        }

        Picture tmpPicture = new Picture(width - 1, height);
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width - 1; col++) {
                validateColumnIndex(seam[row]);
                if (col < seam[row]) {
                    tmpPicture.setRGB(col, row, pictureCopy.getRGB(col, row));
                } else {
                    tmpPicture.setRGB(col, row, pictureCopy.getRGB(col + 1, row));
                }
            }
        }
        pictureCopy = tmpPicture;
        width--;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (seam == null) {
            throw new IllegalArgumentException("the argument to removeVerticalSeam() is null\n");
        }
        if (seam.length != width) {
            throw new IllegalArgumentException("the length of seam not equal height\n");
        }
        validateSeam(seam);
        if (height <= 1) {
            throw new IllegalArgumentException("the width of the picture is less than or equal to 1\n");
        }

        Picture tmpPicture = new Picture(width, height-1);
        for (int col = 0; col < width(); col++) {
            for (int row = 0; row < height-1; row++) {
                validateColumnIndex(seam[col]);
                if (row < seam[col]) {
                    tmpPicture.setRGB(col, row, pictureCopy.getRGB(col, row));
                } else {
                    tmpPicture.setRGB(col, row, pictureCopy.getRGB(col, row+1));
                }
            }
        }
        pictureCopy = tmpPicture;
        height--;
    }

    // transpose the current pictureCopy
    private void transpose() {
        Picture tmpPicture = new Picture(height, width);
        for (int row = 0; row < width; row++) {
            for (int col = 0; col < height; col++) {
                tmpPicture.setRGB(col, row, pictureCopy.getRGB(row, col));
            }
        }
        pictureCopy = tmpPicture;
        int tmp = height;
        height = width;
        width = tmp;
    }

    // make sure column index is bewteen 0 and width - 1
    private void validateColumnIndex(int col) {
        if (col < 0 || col > width -1) {
            throw new IllegalArgumentException("colmun index is outside its prescribed range\n");
        }
    }

    // make sure row index is bewteen 0 and height - 1
    private void validateRowIndex(int row) {
        if (row < 0 || row > height -1) {
            throw new IllegalArgumentException("row index is outside its prescribed range\n");
        }
    }

    // make sure two adjacent entries differ within 1
    private void validateSeam(int[] seam) {
        for (int i = 0; i < seam.length - 1; i++) {
            if (Math.abs(seam[i] - seam[i + 1]) > 1) {
                throw new IllegalArgumentException("two adjacent entries differ by more than 1 in seam\n");
            }
        }
    }
}