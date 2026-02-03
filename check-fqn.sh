#!/bin/bash

# 完全修飾名（Fully Qualified Name）を検出するスクリプト
# import文やpackage文以外で、パッケージ名を含むクラス名を使用している箇所を検出します

set -euo pipefail

# 色の定義
RED='\033[0;31m'
YELLOW='\033[1;33m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# カウンター
total_violations=0
total_files=0

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}完全修飾名検出スクリプト${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# Javaファイルを検索
java_files=$(find app/src/main/java -name "*.java" 2>/dev/null)

if [ -z "$java_files" ]; then
    echo -e "${YELLOW}Javaファイルが見つかりませんでした${NC}"
    exit 0
fi

# 各ファイルをチェック
for file in $java_files; do
    violations=0
    
    # 完全修飾名のパターン（パッケージ名.クラス名）
    # 例: javax.swing.JPanel, java.util.List, com.example.MyClass
    # パターン: 小文字で始まる識別子.小文字で始まる識別子（1回以上）.大文字で始まる識別子
    pattern='[a-z][a-z0-9_]*(\.[a-z][a-z0-9_]*)+\.[A-Z][a-zA-Z0-9_]*'
    
    # 行番号付きで検索
    while IFS= read -r line; do
        line_num=$(echo "$line" | cut -d: -f1)
        line_content=$(echo "$line" | cut -d: -f2-)
        
        # import文、package文、コメント行は除外
        if echo "$line_content" | grep -qE '^\s*(import|package)\s'; then
            continue
        fi
        if echo "$line_content" | grep -qE '^\s*(//|/\*|\*)'; then
            continue
        fi
        
        # 完全修飾名を抽出
        fqns=$(echo "$line_content" | grep -oE "$pattern" || true)
        
        if [ -n "$fqns" ]; then
            if [ $violations -eq 0 ]; then
                echo -e "${RED}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
                echo -e "${RED}✗ $file${NC}"
                echo -e "${RED}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
                ((total_files++))
            fi
            
            # 各完全修飾名を表示
            while IFS= read -r fqn; do
                if [ -n "$fqn" ]; then
                    echo -e "  ${YELLOW}行 $line_num:${NC} $fqn"
                    echo -e "  ${BLUE}→${NC} ${line_content}"
                    echo ""
                    ((violations++))
                    ((total_violations++))
                fi
            done <<< "$fqns"
        fi
    done < <(grep -nE "$pattern" "$file" || true)
done

# サマリー表示
echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}検出結果${NC}"
echo -e "${BLUE}========================================${NC}"

if [ $total_violations -eq 0 ]; then
    echo -e "${GREEN}✓ 完全修飾名は検出されませんでした${NC}"
    exit 0
else
    echo -e "${RED}✗ 完全修飾名が ${total_violations} 箇所で検出されました（${total_files} ファイル）${NC}"
    echo ""
    echo -e "${YELLOW}推奨される修正:${NC}"
    echo "  1. 該当クラスをimport文で宣言してください"
    echo "  2. クラス名の衝突がある場合のみ、完全修飾名の使用が許可されます"
    echo ""
    exit 1
fi
