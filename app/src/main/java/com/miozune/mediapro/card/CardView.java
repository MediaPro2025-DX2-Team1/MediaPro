package com.miozune.mediapro.card;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import com.miozune.mediapro.preview.Previewable;
import com.miozune.mediapro.util.ImageLoader;

/**
 * カード単体を表示するViewコンポーネント。
 * カード名、コスト、画像、説明文を表示する。
 * サイズは親コンテナに応じて可変。
 */
public class CardView extends JPanel implements Previewable {
    
    /** カードのデフォルト幅 */
    private static final int DEFAULT_WIDTH = 150;
    
    /** カードのデフォルト高さ */
    private static final int DEFAULT_HEIGHT = 210;
    
    /** カードのアスペクト比（幅:高さ = 5:7） */
    private static final double ASPECT_RATIO = 5.0 / 7.0;
    
    /** カードの角の丸み */
    private static final int CORNER_RADIUS = 12;
    
    /** カードの枠線の太さ */
    private static final int BORDER_WIDTH = 2;
    
    /** カードの背景色 */
    private static final Color CARD_BACKGROUND = new Color(250, 245, 235);
    
    /** カードの枠線色 */
    private static final Color CARD_BORDER = new Color(60, 60, 60);
    
    /** コスト表示の背景色 */
    private static final Color COST_BACKGROUND = new Color(65, 105, 225);
    
    /** コスト表示の文字色 */
    private static final Color COST_TEXT_COLOR = Color.WHITE;
    
    /** カードデータ */
    private CardModel cardModel;
    
    /** 読み込まれた画像 */
    private BufferedImage cardImage;
    
    /**
     * 空のCardViewを作成する。
     */
    public CardView() {
        this(CardModel.createSample());
    }
    
    /**
     * CardModelを指定してCardViewを作成する。
     *
     * @param cardModel カードデータ
     */
    public CardView(CardModel cardModel) {
        this.cardModel = cardModel;
        
        setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        setMinimumSize(new Dimension(80, 112));
        setOpaque(false);
        
        loadImage();
    }
    
    /**
     * 画像を読み込む。
     */
    private void loadImage() {
        String imageName = cardModel.imageName();
        if (imageName != null && !imageName.isEmpty()) {
            cardImage = ImageLoader.loadCardImage(imageName);
        } else {
            cardImage = null;
        }
    }
    
    // --- Getter / Setter ---
    
    /**
     * カードデータを取得する。
     *
     * @return カードデータ
     */
    public CardModel getCardModel() {
        return cardModel;
    }
    
    /**
     * カードデータを設定する。
     *
     * @param cardModel カードデータ
     */
    public void setCardModel(CardModel cardModel) {
        this.cardModel = cardModel;
        loadImage();
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g;
        
        // アンチエイリアシングを有効化
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        
        // カードのサイズを計算（アスペクト比を維持）
        int cardWidth, cardHeight;
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        
        if (panelWidth / ASPECT_RATIO <= panelHeight) {
            cardWidth = panelWidth;
            cardHeight = (int) (panelWidth / ASPECT_RATIO);
        } else {
            cardHeight = panelHeight;
            cardWidth = (int) (panelHeight * ASPECT_RATIO);
        }
        
        // カードを中央に配置
        int x = (panelWidth - cardWidth) / 2;
        int y = (panelHeight - cardHeight) / 2;
        
        // カードを描画
        drawCard(g2d, x, y, cardWidth, cardHeight);
    }
    
    /**
     * カードを描画する。
     *
     * @param g2d Graphics2Dオブジェクト
     * @param x X座標
     * @param y Y座標
     * @param width カードの幅
     * @param height カードの高さ
     */
    private void drawCard(Graphics2D g2d, int x, int y, int width, int height) {
        int cornerRadius = (int) (CORNER_RADIUS * width / (double) DEFAULT_WIDTH);
        
        // カード背景
        g2d.setColor(CARD_BACKGROUND);
        g2d.fillRoundRect(x, y, width, height, cornerRadius, cornerRadius);
        
        // カード枠線
        g2d.setColor(CARD_BORDER);
        int borderWidth = Math.max(1, (int) (BORDER_WIDTH * width / (double) DEFAULT_WIDTH));
        g2d.setStroke(new java.awt.BasicStroke(borderWidth));
        g2d.drawRoundRect(x, y, width - 1, height - 1, cornerRadius, cornerRadius);
        
        // 内部のパディング
        int padding = width / 12;
        int innerX = x + padding;
        int innerWidth = width - padding * 2;
        
        // 画像エリアの高さ（カード高さの約45%）
        int imageAreaY = y + padding;
        int imageAreaHeight = (int) (height * 0.45);
        
        // 画像を描画
        drawCardImage(g2d, innerX, imageAreaY, innerWidth, imageAreaHeight);
        
        // カード名を描画
        int nameY = imageAreaY + imageAreaHeight + padding / 2;
        int nameHeight = (int) (height * 0.12);
        drawCardName(g2d, innerX, nameY, innerWidth, nameHeight, width);
        
        // 説明文を描画
        int descY = nameY + nameHeight + padding / 4;
        int descHeight = y + height - descY - padding;
        drawDescription(g2d, innerX, descY, innerWidth, descHeight, width);
        
        // コストを描画（左上）
        drawCost(g2d, x, y, width);
    }
    
    /**
     * カード画像を描画する。
     */
    private void drawCardImage(Graphics2D g2d, int x, int y, int width, int height) {
        // 画像エリアの背景
        g2d.setColor(new Color(220, 220, 220));
        g2d.fillRect(x, y, width, height);
        
        if (cardImage != null) {
            // 画像をアスペクト比を維持してフィット
            double imgAspect = (double) cardImage.getWidth() / cardImage.getHeight();
            double areaAspect = (double) width / height;
            
            int drawWidth, drawHeight, drawX, drawY;
            if (imgAspect > areaAspect) {
                drawWidth = width;
                drawHeight = (int) (width / imgAspect);
                drawX = x;
                drawY = y + (height - drawHeight) / 2;
            } else {
                drawHeight = height;
                drawWidth = (int) (height * imgAspect);
                drawX = x + (width - drawWidth) / 2;
                drawY = y;
            }
            
            g2d.drawImage(cardImage, drawX, drawY, drawWidth, drawHeight, null);
        } else {
            // 画像がない場合はプレースホルダーを表示
            g2d.setColor(new Color(180, 180, 180));
            g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, Math.max(10, width / 8)));
            String placeholder = "No Image";
            FontMetrics fm = g2d.getFontMetrics();
            int textX = x + (width - fm.stringWidth(placeholder)) / 2;
            int textY = y + (height + fm.getAscent()) / 2;
            g2d.drawString(placeholder, textX, textY);
        }
        
        // 画像エリアの枠線
        g2d.setColor(CARD_BORDER);
        g2d.drawRect(x, y, width, height);
    }
    
    /**
     * カード名を描画する。
     */
    private void drawCardName(Graphics2D g2d, int x, int y, int width, int height, int cardWidth) {
        g2d.setColor(Color.BLACK);
        int fontSize = Math.max(10, cardWidth / 10);
        g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, fontSize));
        
        FontMetrics fm = g2d.getFontMetrics();
        String name = cardModel.name() != null ? cardModel.name() : "";
        
        // テキストが幅を超える場合は省略
        if (fm.stringWidth(name) > width) {
            while (name.length() > 0 && fm.stringWidth(name + "...") > width) {
                name = name.substring(0, name.length() - 1);
            }
            name = name + "...";
        }
        
        int textX = x + (width - fm.stringWidth(name)) / 2;
        int textY = y + (height + fm.getAscent()) / 2 - fm.getDescent() / 2;
        g2d.drawString(name, textX, textY);
    }
    
    /**
     * 説明文を描画する。
     */
    private void drawDescription(Graphics2D g2d, int x, int y, int width, int height, int cardWidth) {
        g2d.setColor(new Color(60, 60, 60));
        int fontSize = Math.max(8, cardWidth / 14);
        g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, fontSize));
        
        FontMetrics fm = g2d.getFontMetrics();
        String desc = cardModel.description() != null ? cardModel.description() : "";
        
        // 複数行に分割して描画
        int lineHeight = fm.getHeight();
        int currentY = y + fm.getAscent();
        int maxLines = height / lineHeight;
        
        String[] words = desc.split("");
        StringBuilder line = new StringBuilder();
        int lineCount = 0;
        
        for (String word : words) {
            String testLine = line.toString() + word;
            if (fm.stringWidth(testLine) > width && line.length() > 0) {
                // 現在の行を描画
                if (lineCount >= maxLines - 1 && words.length > 0) {
                    // 最後の行で残りがある場合は省略
                    String finalLine = line.toString();
                    while (finalLine.length() > 0 && fm.stringWidth(finalLine + "...") > width) {
                        finalLine = finalLine.substring(0, finalLine.length() - 1);
                    }
                    g2d.drawString(finalLine + "...", x, currentY);
                    return;
                }
                g2d.drawString(line.toString(), x, currentY);
                currentY += lineHeight;
                lineCount++;
                line = new StringBuilder(word);
            } else {
                line.append(word);
            }
        }
        
        // 残りのテキストを描画
        if (line.length() > 0 && lineCount < maxLines) {
            g2d.drawString(line.toString(), x, currentY);
        }
    }
    
    /**
     * コストを描画する。
     */
    private void drawCost(Graphics2D g2d, int cardX, int cardY, int cardWidth) {
        int size = cardWidth / 4;
        int x = cardX + cardWidth / 20;
        int y = cardY + cardWidth / 20;
        
        // コスト背景（円）
        g2d.setColor(COST_BACKGROUND);
        g2d.fillOval(x, y, size, size);
        
        // コスト枠線
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new java.awt.BasicStroke(Math.max(1, size / 15)));
        g2d.drawOval(x, y, size, size);
        
        // コスト数値
        g2d.setColor(COST_TEXT_COLOR);
        int fontSize = Math.max(10, size * 2 / 3);
        g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, fontSize));
        
        String costStr = String.valueOf(cardModel.cost());
        FontMetrics fm = g2d.getFontMetrics();
        int textX = x + (size - fm.stringWidth(costStr)) / 2;
        int textY = y + (size + fm.getAscent()) / 2 - fm.getDescent() / 2;
        g2d.drawString(costStr, textX, textY);
    }
    
    // --- Previewable実装 ---
    
    @Override
    public String getPreviewDescription() {
        return "カード単体を表示するコンポーネント。カード名、コスト、画像、説明文を表示する。";
    }
    
    @Override
    public void setupPreview() {
        setCardModel(CardModel.createSample());
    }
}
