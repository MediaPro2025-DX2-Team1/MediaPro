package com.miozune.mediapro.card;

import com.miozune.mediapro.preview.Previewable;
import com.miozune.mediapro.util.ImageLoader;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 * カード単体を表示するViewコンポーネント。
 * カード名、コスト、画像、説明文を表示する。
 * サイズは親コンテナに応じて可変。
 */
public class CardView extends JPanel implements Previewable {

    /** カードのデフォルト幅 */
    public static final int DEFAULT_WIDTH = 150;

    /** カードのデフォルト高さ */
    public static final int DEFAULT_HEIGHT = 210;

    /** カードのアスペクト比（幅:高さ = 5:7） */
    private static final double ASPECT_RATIO = 5.0 / 7.0;

    /** コスト表示の背景色 */
    private static final Color COST_BACKGROUND = new Color(65, 105, 225);

    /** コスト表示の文字色 */
    private static final Color COST_TEXT_COLOR = Color.WHITE;

    /** カードデータ */
    private final CardModel cardModel;

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
     * 新形式：外枠込み画像を全面表示し、その上にテキストをオーバーレイ。
     *
     * @param g2d Graphics2Dオブジェクト
     * @param x X座標
     * @param y Y座標
     * @param width カードの幅
     * @param height カードの高さ
     */
    private void drawCard(Graphics2D g2d, int x, int y, int width, int height) {
        // 背景画像を全面描画（外枠込み）
        drawCardImage(g2d, x, y, width, height);

        // 左右のパディング
        int textPadding = width / 13;
        int textX = x + textPadding;
        int textWidth = width - textPadding * 2;

        // カード名の位置（カード上端からの割合で直接指定）
        int nameY = y + (int) (height * 0.04);
        drawCardName(g2d, textX, nameY, textWidth, width);

        // 説明文の位置（カード上端からの割合で直接指定）
        int descY = y + (int) (height * 0.75);
        int descBottomY = y + (int) (height * 0.95); // 説明文の下端（下に5%余白）
        int descHeight = descBottomY - descY;
        drawDescription(g2d, textX, descY, textWidth, descHeight, width);

        // コストを描画（左上）
        drawCost(g2d, x, y, width, height);
    }

    /**
     * カード画像を全面描画する（外枠込み画像）。
     */
    private void drawCardImage(Graphics2D g2d, int x, int y, int width, int height) {
        if (cardImage != null) {
            // 画像を全面にフィット描画
            g2d.drawImage(cardImage, x, y, width, height, null);
        } else {
            // 画像がない場合はフォールバック表示
            g2d.setColor(new Color(200, 200, 200));
            g2d.fillRect(x, y, width, height);

            g2d.setColor(new Color(100, 100, 100));
            g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, Math.max(12, width / 10)));
            String placeholder = "No Image";
            FontMetrics fm = g2d.getFontMetrics();
            int textX = x + (width - fm.stringWidth(placeholder)) / 2;
            int textY = y + (height + fm.getAscent()) / 2;
            g2d.drawString(placeholder, textX, textY);
        }
    }

    /**
     * カード名を描画する（画像上にオーバーレイ、視認性向上のため縁取り付き）。
     */
    private void drawCardName(Graphics2D g2d, int x, int y, int width, int cardWidth) {
        int fontSize = Math.max(10, cardWidth / 13);
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

        // 本体テキスト
        int textX = x + (width - fm.stringWidth(name)) / 2;
        int textY = y + fm.getAscent();
        drawTextWithOutline(g2d, name, textX, textY, Math.max(1, fontSize / 14));
    }

    /**
     * 説明文を描画する（画像上にオーバーレイ）。
     */
    private void drawDescription(Graphics2D g2d, int x, int y, int width, int height, int cardWidth) {
        int fontSize = Math.max(8, cardWidth / 16);
        g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, fontSize));

        FontMetrics fm = g2d.getFontMetrics();
        String desc = cardModel.description() != null ? cardModel.description() : "";

        // 複数行に分割して描画
        int lineHeight = fm.getHeight();
        int currentY = y + fm.getAscent();
        int maxLines = Math.max(1, height / lineHeight);

        String[] words = desc.split("");
        StringBuilder line = new StringBuilder();
        int lineCount = 0;
        int outlineWidth = Math.max(1, fontSize / 14);

        for (String word : words) {
            String testLine = line.toString() + word;
            if (fm.stringWidth(testLine) > width && line.length() > 0) {
                // 現在の行を描画
                if (lineCount >= maxLines - 1) {
                    // 最後の行で残りがある場合は省略
                    String finalLine = line.toString();
                    while (finalLine.length() > 0 && fm.stringWidth(finalLine + "...") > width) {
                        finalLine = finalLine.substring(0, finalLine.length() - 1);
                    }
                    g2d.drawString(finalLine + "...", x, currentY);
                    return;
                }
                // drawTextWithOutline(g2d, line.toString(), x, currentY, outlineWidth);
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
            // drawTextWithOutline(g2d, line.toString(), x, currentY, outlineWidth);
            g2d.drawString(line.toString(), x, currentY);
        }
    }

    /**
     * テキストを縁取り付きで描画する。
     */
    private void drawTextWithOutline(Graphics2D g2d, String text, int x, int y, int outlineWidth) {
        // 白い縁取り
        g2d.setColor(Color.WHITE);
        for (int dx = -outlineWidth; dx <= outlineWidth; dx++) {
            for (int dy = -outlineWidth; dy <= outlineWidth; dy++) {
                if (dx != 0 || dy != 0) {
                    g2d.drawString(text, x + dx, y + dy);
                }
            }
        }
        // 本体テキスト（黒）
        g2d.setColor(Color.BLACK);
        g2d.drawString(text, x, y);
    }

    /**
     * コストを描画する（左上の円形バッジ）。
     */
    private void drawCost(Graphics2D g2d, int cardX, int cardY, int cardWidth, int cardHeight) {
        int size = cardWidth / 8;
        int x = cardX + cardWidth / 25;
        int y = cardY + cardHeight / 25;

        // コスト背景（円）
        g2d.setColor(COST_BACKGROUND);
        g2d.fillOval(x, y, size, size);

        // コスト枠線
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(Math.max(1, size / 12)));
        g2d.drawOval(x, y, size, size);

        // コスト数値
        g2d.setColor(COST_TEXT_COLOR);
        int fontSize = Math.max(12, size * 2 / 3);
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
    public void setupPreview() {}
}
