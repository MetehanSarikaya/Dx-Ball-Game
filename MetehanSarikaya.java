import java.awt.Color;

public class MetehanSarikaya{
    public static void main(String[] args) {
        // Canvas settings
        double xScale = 800.0, yScale = 400.0;
        StdDraw.setCanvasSize(800, 400);
        StdDraw.setXscale(0.0, xScale);
        StdDraw.setYscale(0.0, yScale);
        StdDraw.enableDoubleBuffering();

        // Game parameters
        double ballRadius = 8;
        double ballVelocity = 5;
        double[] ballPos = {400, 18};
        Color ballColor = new Color(15, 82, 186);
        Color[] colors = { new Color(255, 0, 0), new Color(220, 20, 60),
                new Color(178, 34, 34), new Color(139, 0, 0),
                new Color(255, 69, 0), new Color(165, 42, 42)
        };

        double[] paddlePos = {400, 5};
        double paddleHalfWidth = 60;
        double paddleHalfHeight = 5;
        double paddleSpeed = 20;
        Color paddleColor = new Color(128, 128, 128);

        double brickHalfWidth = 50, brickHalfHeight = 10;
        double[][] brickCoordinates = new double[][]{
                {250, 320}, {350, 320}, {450, 320}, {550, 320},
                {150, 300}, {250, 300}, {350, 300}, {450, 300}, {550, 300}, {650, 300},
                {50, 280}, {150, 280}, {250, 280}, {350, 280}, {450, 280}, {550, 280}, {650, 280}, {750, 280},
                {50, 260}, {150, 260}, {250, 260}, {350, 260}, {450, 260}, {550, 260}, {650, 260}, {750, 260},
                {50, 240}, {150, 240}, {250, 240}, {350, 240}, {450, 240}, {550, 240}, {650, 240}, {750, 240},
                {150, 220}, {250, 220}, {350, 220}, {450, 220}, {550, 220}, {650, 220},
                {250, 200}, {350, 200}, {450, 200}, {550, 200}
        };

        Color[] brickColors = new Color[]{
                colors[0], colors[1], colors[2], colors[3],
                colors[2], colors[4], colors[3], colors[0], colors[4], colors[5],
                colors[5], colors[0], colors[1], colors[5], colors[2], colors[3], colors[0], colors[4],
                colors[1], colors[3], colors[2], colors[4], colors[0], colors[5], colors[2], colors[1],
                colors[4], colors[0], colors[5], colors[1], colors[2], colors[3], colors[0], colors[5],
                colors[1], colors[4], colors[0], colors[5], colors[1], colors[2],
                colors[3], colors[2], colors[3], colors[0]
        };

        // 1.) Initially all bricks are visible (not broken)
        boolean[] brickVisible = new boolean[brickCoordinates.length];
        for (int i = 0; i < brickVisible.length; i++) {
            brickVisible[i] = false;
        }
        //1.1) game status
        int score = 0;
        boolean gameHasStarted = false;
        boolean gameOver = false;
        boolean gameHasPaused = false;
        boolean victory = false;
        boolean spaceWasPressed = false;

        //to ease calculations.
        double shootingAngle = Math.PI / 2;

        //initial velocity
        double[] ballVelocityComponents = {0, 0};


        //2.)continuity of the game is provided by while (true) loop.
        while (true) {
            StdDraw.clear();

            boolean isSpaceDown = StdDraw.isKeyPressed(32); // Space key
            boolean isLeftDown = StdDraw.isKeyPressed(37); // Left arrow
            boolean isRightDown = StdDraw.isKeyPressed(39); // Right arrow

            //2.1)If space is pressed and it is for  first time (spaceWasPressed = false) that means game is starting.
            if (isSpaceDown && !spaceWasPressed) {
                if (!gameHasStarted) {
                    //by changing gameStarted status,  declared that the game started.
                    gameHasStarted = true;

                    /*
                    3.) components of the ball's velocity , basic physics rules.
                    ballVelocityComponents and pallPos will be actively used.
                     */

                    ballVelocityComponents[0] = ballVelocity * Math.cos(shootingAngle);
                    ballVelocityComponents[1] = ballVelocity * Math.sin(shootingAngle);

                    //2.2)game status has already changed as started , so if space is pressed again then the code will arrive this else if statement.
                } else if (!gameOver) {
                    gameHasPaused = !gameHasPaused;
                }
                spaceWasPressed = true;

            } else if (!isSpaceDown) {
                spaceWasPressed = false;
            }

            //2.3) Arranging shooting angle:
            if (!gameHasStarted) {
                if (isLeftDown) {
                    shootingAngle = Math.min(shootingAngle + 0.025, Math.PI);
                }
                if (isRightDown) {
                    shootingAngle = Math.max(0,shootingAngle - 0.025 );
                }

                //4.) Drawing the aim tool.
                StdDraw.setPenColor(Color.RED);
                double lineLength = 50;
                double lineEndX = ballPos[0] + lineLength * Math.cos(shootingAngle);
                double lineEndY = ballPos[1] + lineLength * Math.sin(shootingAngle);
                StdDraw.line(ballPos[0], ballPos[1], lineEndX, lineEndY);
                StdDraw.setPenColor(Color.BLACK);
                StdDraw.text(50, 380, "Angle: " + Math.round(Math.toDegrees(shootingAngle)) + "°");
            }

            //5.) Paddle movement and checking that the paddle inside the canvas.
            if (isRightDown && gameHasStarted && !gameOver && !gameHasPaused) {
                paddlePos[0] = Math.min(paddlePos[0] + paddleSpeed, xScale - paddleHalfWidth);
            }

            if (isLeftDown && gameHasStarted && !gameOver && !gameHasPaused) {
                paddlePos[0] = Math.max(paddlePos[0] - paddleSpeed, paddleHalfWidth);
            }

            // 6.) Updating game state if game is started and not paused or over.
            if (gameHasStarted && !gameOver && !gameHasPaused) {
                // Updating ball position by given formula in description.
                ballPos[0] += ballVelocityComponents[0];
                ballPos[1] += ballVelocityComponents[1];

                //after updating ball position according to shooting angle, now collision types must be seperated.
                /*firstly, side walls; if x component of ballPos (ballPos[0]) is closer the left wall than its radius or
                 if y component of ballPos (ballPos[1]) closer the right wall than its radius , it is accepted as the ball hit the side walls.
                 */

                //7.) side walls (x-component)
                if (ballPos[0] <= ballRadius || ballPos[0] + ballRadius >= xScale) {

                    //if the ball hits the left side
                    if (ballPos[0] - ballRadius <= 0) {

                        //adjusting the new position of the ball
                        ballPos[0] = ballRadius;
                        //adjusting the new speed of the ball according to basic physics law.
                        ballVelocityComponents[0] = -ballVelocityComponents[0];

                    } else if (ballPos[0] + ballRadius >= xScale) {
                        //adjusting the new position of the ball
                        ballPos[0] = xScale - ballRadius;
                        //adjusting the new speed of the ball according to basic physics law.
                        ballVelocityComponents[0] = -ballVelocityComponents[0];
                    }
                }

                //8.) if position of the ball y component is closer than its radius to the top edge, it is accepted as it hits to the top edge.
                if (ballPos[1] + ballRadius >= yScale) {

                    ballPos[1] = yScale - ballRadius;
                    ballVelocityComponents[1] = -ballVelocityComponents[1];
                }

                //9.) Detect ball falling below bottom edge
                if (ballPos[1] - ballRadius <= 0) {
                    gameOver = true;
                }


                // 10.)Paddle collision
                //Checks if the ball overlaps with the paddle (potential collision).
                //if this boolean returns true, that means ball hits the paddle.
                boolean ballHitsThePaddle = (ballPos[0] + ballRadius >= paddlePos[0] - paddleHalfWidth &&
                        ballPos[0] - ballRadius <= paddlePos[0] + paddleHalfWidth &&
                        ballPos[1] - ballRadius <= paddlePos[1] + paddleHalfHeight &&
                        ballPos[1] + ballRadius >= paddlePos[1] - paddleHalfHeight);

                //11.)
                if (ballHitsThePaddle) {

                    // 12.) Check if the ball is approaching the paddle from above
                    //If this if statement is not written,the ball will fall if it goes parallel to the paddle
                    //for example: in the case if initial angle is equal to 0.
                    if (ballVelocityComponents[1] < 0) {

                        //12.1) Check if it's a flat collision or corner collision
                        if (ballPos[0] > paddlePos[0] - paddleHalfWidth &&
                                ballPos[0] < paddlePos[0] + paddleHalfWidth) {
                            // Top collision - simple reflection
                            ballPos[1] = paddlePos[1] + paddleHalfHeight + ballRadius;
                            ballVelocityComponents[1] = -ballVelocityComponents[1];

                            //13.) Corner collision handling
                        } else {
                            // Determine exact collision point on the paddle's corner
                            double collisionPointX;
                            double collisionPointY;

                            // 13.1)Determine which corner was hit
                            if (ballPos[0] < paddlePos[0]) {
                                // Left corner collision
                                collisionPointX = paddlePos[0] - paddleHalfWidth;
                                collisionPointY = paddlePos[1] + paddleHalfHeight;
                            } else {
                                // Right corner collision
                                collisionPointX = paddlePos[0] + paddleHalfWidth;
                                collisionPointY = paddlePos[1] + paddleHalfHeight;
                            }

                            //13.2) Calculate the distance from collision point to ball center
                            double distX = ballPos[0] - collisionPointX;
                            double distY = ballPos[1] - collisionPointY;

                            // 13.3)nX and nY they are normal vectors for each component.
                            double length = Math.sqrt(distX * distX + distY * distY);
                            double nX;
                            double nY;
                            nX = distX / length;
                            nY = distY / length;

                            // 13.4) Calculate the dot product
                            double dotProduct = ballVelocityComponents[0] * nX + ballVelocityComponents[1] * nY;

                            // 13.5) Formula for reflection: b = a - 2 * (a·n) * n
                            // Where a is the incident vector, n is the normal, and b is the reflection vector
                            ballVelocityComponents[0] = ballVelocityComponents[0] - 2 * dotProduct * nX;
                            ballVelocityComponents[1] = ballVelocityComponents[1] - 2 * dotProduct * nY;

                            // 14.) Adjust ball position to prevent sticking to the paddle
                            ballPos[1] = paddlePos[1] + paddleHalfHeight + ballRadius;

                        }
                    }
                }

                // 15.) Find colliding bricks
                boolean allBricksDestroyed = true;

                // 16.) Create arrays to store colliding brick indices
                int[] collidingBricks = new int[brickCoordinates.length];
                int collidingCount = 0;

                // 17.) First check if any bricks are still visible
                for (int i = 0; i < brickCoordinates.length; i++) {
                    if (!brickVisible[i]) {
                        allBricksDestroyed = false;
                        break;
                    }
                }

                //18.) If all bricks are destroyed, set victory condition
                if (allBricksDestroyed) {
                    gameOver = true;
                    victory = true;
                }

                // 19.) Find all colliding bricks
                for (int i = 0; i < brickCoordinates.length; i++) {
                    if (!brickVisible[i]) {
                        double brickX = brickCoordinates[i][0];
                        double brickY = brickCoordinates[i][1];

                        // 20.) Check for collision with this brick
                        boolean collisionWithTheBrick = (ballPos[0] + ballRadius >= brickX - brickHalfWidth &&
                                ballPos[0] - ballRadius <= brickX + brickHalfWidth &&
                                ballPos[1] + ballRadius >= brickY - brickHalfHeight &&
                                ballPos[1] - ballRadius <= brickY + brickHalfHeight);

                        if (collisionWithTheBrick) {
                            // Store this colliding brick
                            collidingBricks[collidingCount] = i;
                            collidingCount++;
                        }
                    }
                }
                //21.) If collision is detected.
                if (collidingCount > 0) {
                    // Create variables to track overall collision direction
                    boolean hasXCollision = false;
                    boolean hasYCollision = false;
                    boolean hasCornerCollision = false;

                    //22.) Process all colliding bricks first to determine overall collision type
                    for (int k = 0; k < collidingCount; k++) {
                        int i = collidingBricks[k];
                        double brickX = brickCoordinates[i][0];
                        double brickY = brickCoordinates[i][1];

                        // Mark brick as broken
                        brickVisible[i] = true;
                        score += 10;

                        // 23.)to determine collision side
                        //when the ball coming from the left
                        double leftEdge = Math.abs((brickX - brickHalfWidth) - (ballPos[0] + ballRadius));
                        //when the ball coming from the right
                        double rightEdge = Math.abs((ballPos[0] - ballRadius) - (brickX + brickHalfWidth));
                        //when the ball coming from the top
                        double topEdge = Math.abs((ballPos[1] - ballRadius) - (brickY + brickHalfHeight));
                        //when the ball coming from the bottom
                        double bottomEdge = Math.abs((brickY - brickHalfHeight) - (ballPos[1] + ballRadius));

                        // 24.) Determine if it's a corner collision
                        boolean brickCornerCollision = false;
                        double cornerX= 0;
                        double cornerY=0;

                        // 24.1) Check top-left corner
                        if (ballPos[0] < brickX - brickHalfWidth && ballPos[1] > brickY + brickHalfHeight) {
                            cornerX = brickX - brickHalfWidth;
                            cornerY = brickY + brickHalfHeight;
                            brickCornerCollision = true;
                        }
                        // 24.2) Check top-right corner
                        else if (ballPos[0] > brickX + brickHalfWidth && ballPos[1] > brickY + brickHalfHeight) {
                            cornerX = brickX + brickHalfWidth;
                            cornerY = brickY + brickHalfHeight;
                            brickCornerCollision = true;
                        }
                        // 24.3) Check bottom-left corner
                        else if (ballPos[0] < brickX - brickHalfWidth && ballPos[1] < brickY - brickHalfHeight) {
                            cornerX = brickX - brickHalfWidth;
                            cornerY = brickY - brickHalfHeight;
                            brickCornerCollision = true;
                        }
                        // 24.4) Check bottom-right corner
                        else if (ballPos[0] > brickX + brickHalfWidth && ballPos[1] < brickY - brickHalfHeight) {
                            cornerX = brickX + brickHalfWidth;
                            cornerY = brickY - brickHalfHeight;
                            brickCornerCollision = true;
                        }
                        // 25.) if it is corner collision.
                        if (brickCornerCollision) {
                            // Check if ball is actually touching the corner
                            double distToBrickCorner = Math.sqrt(Math.pow(ballPos[0] - cornerX, 2) + Math.pow(ballPos[1] - cornerY, 2));
                            //26.) It prevents unexpected corner collisions.
                            if (distToBrickCorner <= ballRadius) {
                                hasCornerCollision = true;

                                // 27.)Calculate the distance from collision point to ball center.
                                double distX = ballPos[0] - cornerX;
                                double distY = ballPos[1] - cornerY;

                                // nX and nY they are normal vectors for each component.
                                double length = Math.sqrt(distX * distX + distY * distY);
                                double nX = distX / length;
                                double nY = distY / length;

                                // Calculate the dot product of velocity and normal.
                                double dotProduct = ballVelocityComponents[0] * nX + ballVelocityComponents[1] * nY;

                                // Formula for reflection:  b = a - 2 * (a·n) * n
                                // Where a is the incident vector, n is the normal, and b is the reflection vector
                                ballVelocityComponents[0] = ballVelocityComponents[0] - 2 * dotProduct * nX;
                                ballVelocityComponents[1] = ballVelocityComponents[1] - 2 * dotProduct * nY;

                                // 28.) Prevent further collision processing for this ball movement
                                break;
                            }
                            // 29.) if it is not corner collision.
                        } else {
                            // Find smallest overlap to determine collision side
                            double minOverlap = Math.min(Math.min(leftEdge, rightEdge), Math.min(topEdge, bottomEdge));

                            // Set collision flags based on direction
                            if (minOverlap == leftEdge || minOverlap == rightEdge) {
                                hasXCollision = true;
                            }
                            if (minOverlap == topEdge || minOverlap == bottomEdge) {
                                hasYCollision = true;
                            }
                        }
                    }


                    // 30.) Only apply velocity changes if we didn't have a corner collision
                    if (!hasCornerCollision) {
                        // Apply velocity changes for edge collisions
                        if (hasXCollision) {
                            ballVelocityComponents[0] = -ballVelocityComponents[0];
                        }
                        if (hasYCollision) {
                            ballVelocityComponents[1] = -ballVelocityComponents[1];
                        }
                    }
                }
            }

            // 31.) Draw paddle
            StdDraw.setPenColor(paddleColor);
            StdDraw.filledRectangle(paddlePos[0], paddlePos[1], paddleHalfWidth, paddleHalfHeight);

            // 32.) Draw ball
            StdDraw.setPenColor(ballColor);
            StdDraw.filledCircle(ballPos[0], ballPos[1], ballRadius);

            // 33.) Draw bricks
            for (int i = 0; i < brickCoordinates.length; i++) {
                if (!brickVisible[i]) {
                    StdDraw.setPenColor(brickColors[i % brickColors.length]);
                    StdDraw.filledRectangle(brickCoordinates[i][0], brickCoordinates[i][1], brickHalfWidth, brickHalfHeight);
                    StdDraw.setPenColor(Color.BLACK);
                    StdDraw.rectangle(brickCoordinates[i][0], brickCoordinates[i][1], brickHalfWidth, brickHalfHeight);
                }
            }

            // 34.) Draw score
            StdDraw.setPenColor(Color.BLACK);
            StdDraw.text(750, 380, "Score: " + score);

            // 35.)Game pause message
            if (gameHasPaused) {
                StdDraw.setPenColor(Color.BLACK);
                StdDraw.text(50, 380, "PAUSED");
            }

            // 36.) Game end messages
            if (gameOver) {
                StdDraw.setPenColor(new Color(0, 0, 0, 150));
                StdDraw.filledRectangle(xScale / 2, yScale / 2, xScale / 2, yScale / 2);
                StdDraw.setPenColor(Color.WHITE);

                if (victory) {
                    StdDraw.text(xScale / 2, yScale / 2 + 30, "VICTORY!");
                } else {
                    StdDraw.text(xScale / 2, yScale / 2 + 30, "GAME OVER!");
                }

                StdDraw.text(xScale / 2, yScale / 2 - 30, "Score: " + score);
            }

            //37.)
            StdDraw.show();
            StdDraw.pause(18);
        }
    }
}