package game.template;

public class Board {
    private ChessPiece[][] board;
    private Player[][] owners;

    public Board() {
        board = new ChessPiece[8][8];
        owners = new Player[8][8];
        initialize();
    }

    private void initialize() {
        for (int col = 0; col < 8; col++) {
            board[1][col] = ChessPiece.PAWN;
            owners[1][col] = Player.BLACK;
            board[6][col] = ChessPiece.PAWN;
            owners[6][col] = Player.WHITE;
        }

        ChessPiece[] layout = {
            ChessPiece.ROOK, ChessPiece.KNIGHT, ChessPiece.BISHOP, ChessPiece.QUEEN,
            ChessPiece.KING, ChessPiece.BISHOP, ChessPiece.KNIGHT, ChessPiece.ROOK
        };

        for (int col = 0; col < 8; col++) {
            board[0][col] = layout[col];
            owners[0][col] = Player.BLACK;
            board[7][col] = layout[col];
            owners[7][col] = Player.WHITE;
        }
    }

    public boolean isOccupied(int row, int col) {
        return board[row][col] != null;
    }

    public ChessPiece getPiece(int row, int col) {
        return board[row][col];
    }

    public Player getOwner(int row, int col) {
        return owners[row][col];
    }

    public void setPiece(int row, int col, ChessPiece piece, Player owner) {
        board[row][col] = piece;
        owners[row][col] = owner;
    }

    public void clearSquare(int row, int col) {
        board[row][col] = null;
        owners[row][col] = null;
    }

    // Add isValidPawnMove from previous message here too.
    public boolean isValidPawnMove(int fromRow, int fromCol, int toRow, int toCol) {
        if (!isInBounds(fromRow, fromCol) || !isInBounds(toRow, toCol)) return false;

        ChessPiece piece = board[fromRow][fromCol];
        Player owner = owners[fromRow][fromCol];

        if (piece != ChessPiece.PAWN) return false;

        int direction = (owner == Player.WHITE) ? -1 : 1; // white moves up, black moves down
        int startRow = (owner == Player.WHITE) ? 6 : 1;

        // Normal forward move
        if (fromCol == toCol && board[toRow][toCol] == null) {
            if (toRow == fromRow + direction) return true;
            if (fromRow == startRow && toRow == fromRow + 2 * direction && board[fromRow + direction][toCol] == null)
                return true;
        }

        // Diagonal capture
        if (Math.abs(fromCol - toCol) == 1 && toRow == fromRow + direction && board[toRow][toCol] != null) {
            return owners[toRow][toCol] != owner; // must be an enemy piece
        }

        return false;
    }

    public boolean isValidRookMove(int fromRow, int fromCol, int toRow, int toCol) {
        if (!isInBounds(fromRow, fromCol) || !isInBounds(toRow, toCol)) return false;

        ChessPiece piece = board[fromRow][fromCol];
        Player owner = owners[fromRow][fromCol];

        if (piece != ChessPiece.ROOK && piece != ChessPiece.QUEEN) return false;

        // Can't move to your own square
        if (fromRow == toRow && fromCol == toCol) return false;

        // Vertical move
        if (fromCol == toCol) {
            int step = (toRow > fromRow) ? 1 : -1;
            for (int r = fromRow + step; r != toRow; r += step) {
                if (board[r][fromCol] != null) return false; // Blocked
            }
        }
        // Horizontal move
        else if (fromRow == toRow) {
            int step = (toCol > fromCol) ? 1 : -1;
            for (int c = fromCol + step; c != toCol; c += step) {
                if (board[fromRow][c] != null) return false; // Blocked
            }
        } else {
            return false; // Not straight move
        }

        // Destination square: must be empty or contain opponent piece
        return board[toRow][toCol] == null || owners[toRow][toCol] != owner;
    }


        private boolean isInBounds(int row, int col) {
            return row >= 0 && row < 8 && col >= 0 && col < 8;
        }
        public void clearAll() {
            for (int r = 0; r < 8; r++) {
                for (int c = 0; c < 8; c++) {
                    board[r][c] = null;
                    owners[r][c] = null;
                }
            }
        }
        public boolean isValidKnightMove(int fromRow, int fromCol, int toRow, int toCol) {
            if (!isInBounds(fromRow, fromCol) || !isInBounds(toRow, toCol)) return false;

            ChessPiece piece = board[fromRow][fromCol];
            Player owner = owners[fromRow][fromCol];

            if (piece != ChessPiece.KNIGHT) return false;

            int rowDiff = Math.abs(toRow - fromRow);
            int colDiff = Math.abs(toCol - fromCol);

            // Must move in L-shape: 2 by 1 or 1 by 2
            boolean isLShape = (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);
            if (!isLShape) return false;

            // Target must be empty or contain enemy
            return board[toRow][toCol] == null || owners[toRow][toCol] != owner;
        }

        public boolean isValidBishopMove(int fromRow, int fromCol, int toRow, int toCol) {
            if (!isInBounds(fromRow, fromCol) || !isInBounds(toRow, toCol)) return false;

            ChessPiece piece = board[fromRow][fromCol];
            Player owner = owners[fromRow][fromCol];

            if (piece != ChessPiece.BISHOP && piece != ChessPiece.QUEEN) return false;

            int rowDiff = toRow - fromRow;
            int colDiff = toCol - fromCol;

            if (Math.abs(rowDiff) != Math.abs(colDiff)) return false;

            int rowStep = Integer.signum(rowDiff);
            int colStep = Integer.signum(colDiff);

            int r = fromRow + rowStep;
            int c = fromCol + colStep;

            while (r != toRow && c != toCol) {
                if (!isInBounds(r, c)) return false; //  bounds check
                if (board[r][c] != null) return false;
                r += rowStep;
                c += colStep;
            }

            // Final destination
            return isInBounds(toRow, toCol) &&
                (board[toRow][toCol] == null || owners[toRow][toCol] != owner);
        }
        public boolean isSquareUnderAttack(int row, int col, Player kingOwner) {
            for (int r = 0; r < 8; r++) {
                for (int c = 0; c < 8; c++) {
                    if (board[r][c] != null && owners[r][c] != kingOwner) {
                        ChessPiece attacker = board[r][c];
                        boolean canAttack = false;
                        switch (attacker) {
                            case PAWN:
                                canAttack = isValidPawnMove(r, c, row, col);
                                break;
                            case ROOK:
                                canAttack = isValidRookMove(r, c, row, col);
                                break;
                            case BISHOP:
                                canAttack = isValidBishopMove(r, c, row, col);
                                break;
                            case KNIGHT:
                                canAttack = isValidKnightMove(r, c, row, col);
                                break;
                            case KING:
                                canAttack = isValidKingMove(r, c, row, col); // small step
                                break;
                            case QUEEN:
                                canAttack = isValidQueenMove(r, c, row, col);
                                break;
                        }
                        if (canAttack) return true;
                    }
                }
            }
            return false;
        }



        public boolean isValidKingMove(int fromRow, int fromCol, int toRow, int toCol) {
            if (!isInBounds(fromRow, fromCol) || !isInBounds(toRow, toCol)) return false;

            ChessPiece piece = board[fromRow][fromCol];
            Player owner = owners[fromRow][fromCol];

            if (piece != ChessPiece.KING) return false;

            int rowDiff = Math.abs(toRow - fromRow);
            int colDiff = Math.abs(toCol - fromCol);

            // Only one square in any direction
            if (rowDiff <= 1 && colDiff <= 1 && (rowDiff + colDiff > 0)) {
                // Simulate move
                ChessPiece targetPiece = board[toRow][toCol];
                Player targetOwner = owners[toRow][toCol];

                board[toRow][toCol] = piece;
                board[fromRow][fromCol] = null;
                owners[toRow][toCol] = owner;
                owners[fromRow][fromCol] = null;

                boolean isSafe = !isSquareUnderAttack(toRow, toCol, owner);

                // Revert
                board[fromRow][fromCol] = piece;
                board[toRow][toCol] = targetPiece;
                owners[fromRow][fromCol] = owner;
                owners[toRow][toCol] = targetOwner;

                // Must be safe
                return isSafe && (targetPiece == null || targetOwner != owner);
            }

            return false;
        }


        public boolean isValidQueenMove(int fromRow, int fromCol, int toRow, int toCol) {
            if (!isInBounds(fromRow, fromCol) || !isInBounds(toRow, toCol)) return false;

            ChessPiece piece = board[fromRow][fromCol];
            Player owner = owners[fromRow][fromCol];
            if (piece != ChessPiece.QUEEN) return false;

            // Check for valid rook-like move
            if ((fromRow == toRow || fromCol == toCol) && isValidRookMove(fromRow, fromCol, toRow, toCol)) {
                return true;
            }

            // Check for valid bishop-like move
            if ((Math.abs(toRow - fromRow) == Math.abs(toCol - fromCol)) &&
                isValidBishopMove(fromRow, fromCol, toRow, toCol)) {
                return true;
            }

            return false;
        }
        public boolean isKingAlive(Player player) {
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    if (board[row][col] == ChessPiece.KING && owners[row][col] == player) {
                        return true;
                    }
                }
            }
            return false;
        }

        public boolean isInCheck(Player player) {
            int kingRow = -1, kingCol = -1;

            // Find the king's position
            for (int r = 0; r < 8; r++) {
                for (int c = 0; c < 8; c++) {
                    if (board[r][c] == ChessPiece.KING && owners[r][c] == player) {
                        kingRow = r;
                        kingCol = c;
                        break;
                    }
                }
            }

            if (kingRow == -1 || kingCol == -1) return true; // King not found (already captured)

            // Check if any opposing piece can attack the king's position
            for (int r = 0; r < 8; r++) {
                for (int c = 0; c < 8; c++) {
                    if (owners[r][c] != player && owners[r][c] != null) {
                        ChessPiece piece = board[r][c];
                        boolean canAttack = switch (piece) {
                            case PAWN -> isValidPawnMove(r, c, kingRow, kingCol);
                            case ROOK -> isValidRookMove(r, c, kingRow, kingCol);
                            case BISHOP -> isValidBishopMove(r, c, kingRow, kingCol);
                            case KNIGHT -> isValidKnightMove(r, c, kingRow, kingCol);
                            case QUEEN -> isValidQueenMove(r, c, kingRow, kingCol);
                            case KING -> isValidKingMove(r, c, kingRow, kingCol);
                        };
                        if (canAttack) return true;
                    }
                }
            }

            return false;
        }

        public boolean isCheckmate(Player player) {
            if (!isInCheck(player)) return false;

            for (int fromRow = 0; fromRow < 8; fromRow++) {
                for (int fromCol = 0; fromCol < 8; fromCol++) {
                    if (owners[fromRow][fromCol] == player) {
                        ChessPiece piece = board[fromRow][fromCol];
                        for (int toRow = 0; toRow < 8; toRow++) {
                            for (int toCol = 0; toCol < 8; toCol++) {
                                boolean valid = switch (piece) {
                                    case PAWN -> isValidPawnMove(fromRow, fromCol, toRow, toCol);
                                    case ROOK -> isValidRookMove(fromRow, fromCol, toRow, toCol);
                                    case BISHOP -> isValidBishopMove(fromRow, fromCol, toRow, toCol);
                                    case KNIGHT -> isValidKnightMove(fromRow, fromCol, toRow, toCol);
                                    case QUEEN -> isValidQueenMove(fromRow, fromCol, toRow, toCol);
                                    case KING -> isValidKingMove(fromRow, fromCol, toRow, toCol);
                                };
                                if (valid) {
                                    // Simulate move
                                    ChessPiece tempPiece = board[toRow][toCol];
                                    Player tempOwner = owners[toRow][toCol];
                                    board[toRow][toCol] = piece;
                                    owners[toRow][toCol] = player;
                                    board[fromRow][fromCol] = null;
                                    owners[fromRow][fromCol] = null;

                                    boolean stillInCheck = isInCheck(player);

                                    // Undo move
                                    board[fromRow][fromCol] = piece;
                                    owners[fromRow][fromCol] = player;
                                    board[toRow][toCol] = tempPiece;
                                    owners[toRow][toCol] = tempOwner;

                                    if (!stillInCheck) return false;
                                }
                            }
                        }
                    }
                }
            }

            return true;
        }
        public boolean isMoveLegalWhenInCheck(Player player, int fromRow, int fromCol, int toRow, int toCol) {
            ChessPiece piece = board[fromRow][fromCol];

            // Simulate move
            ChessPiece captured = board[toRow][toCol];
            Player capturedOwner = owners[toRow][toCol];

            board[toRow][toCol] = piece;
            owners[toRow][toCol] = player;
            board[fromRow][fromCol] = null;
            owners[fromRow][fromCol] = null;

            boolean stillInCheck = isInCheck(player);

            // Revert move
            board[fromRow][fromCol] = piece;
            owners[fromRow][fromCol] = player;
            board[toRow][toCol] = captured;
            owners[toRow][toCol] = capturedOwner;

            return !stillInCheck;
        }





    }
