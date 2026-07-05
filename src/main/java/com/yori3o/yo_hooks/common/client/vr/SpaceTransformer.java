package com.yori3o.yo_hooks.common.client.vr;

import net.minecraft.world.phys.Vec3;

public class SpaceTransformer {

    private static final double EPSILON = 1e-5;

    // Точки отсчета (начала локальных координат)
    private final Vec3 originGame;
    private final Vec3 originRoom;

    // Масштаб (отношение размеров мира к игре)
    private final double scale;

    // Базис игрового мира (Game Space)
    private final Vec3 forwardGame;
    private final Vec3 rightGame;
    private final Vec3 upGame;

    // Базис комнаты (Room Space)
    private final Vec3 forwardRoom;
    private final Vec3 rightRoom;
    private final Vec3 upRoom;

    /**
     * Конструктор вычисляет и кэширует базисы обоих пространств.
     * Выбрасывает IllegalArgumentException, если базис построить невозможно.
     */
    public SpaceTransformer(Vec3 gameA, Vec3 gameB, Vec3 gameC,
                            Vec3 roomM, Vec3 roomN, Vec3 roomK) throws IllegalArgumentException {
        
        this.originGame = gameA;
        this.originRoom = roomM;

        // 1. Вычисление векторов направления и масштаба
        Vec3 gameDir = gameB.subtract(gameA);
        Vec3 roomDir = roomN.subtract(roomM);
        
        double lengthGame = gameDir.length();
        double lengthRoom = roomDir.length();

        if (lengthGame < EPSILON || lengthRoom < EPSILON) {
            throw new IllegalArgumentException("Points A and B (or M and N) have merged into one. It is impossible to determine the scale and direction.");
        }
        
        this.scale = lengthRoom / lengthGame;

        // 2. Построение базиса Игры
        this.forwardGame = gameDir.normalize();
        Vec3 toHeadGame = gameC.subtract(gameA);
        Vec3 crossGame = toHeadGame.cross(this.forwardGame);
        
        if (crossGame.length() < EPSILON) {
            throw new IllegalArgumentException("The points in the game world lie on the same line. The basis is not defined.");
        }
        
        this.rightGame = crossGame.normalize();
        this.upGame = forwardGame.cross(rightGame).normalize();

        // 3. Построение базиса Комнаты
        this.forwardRoom = roomDir.normalize();
        Vec3 toHeadRoom = roomK.subtract(roomM);
        Vec3 crossRoom = toHeadRoom.cross(this.forwardRoom);
        
        if (crossRoom.length() < EPSILON) {
            throw new IllegalArgumentException("The points in the room lie on the same line. The basis is not defined.");
        }
        
        this.rightRoom = crossRoom.normalize();
        this.upRoom = forwardRoom.cross(this.rightRoom).normalize();
    }

    /**
     * Переводит точку из Игры в Комнату.
     */
    public Vec3 gameToRoom(Vec3 gamePosition) {
        Vec3 vecAX = gamePosition.subtract(originGame);
        
        double locX = vecAX.dot(rightGame);
        double locY = vecAX.dot(upGame);
        double locZ = vecAX.dot(forwardGame);

        return originRoom
                .add(rightRoom.scale(locX * scale))
                .add(upRoom.scale(locY * scale))
                .add(forwardRoom.scale(locZ * scale));
    }

    /**
     * Переводит точку из Комнаты обратно в Игру.
     */
    public Vec3 roomToGame(Vec3 roomPosition) {
        Vec3 vecMY = roomPosition.subtract(originRoom);
        
        double locX = vecMY.dot(rightRoom);
        double locY = vecMY.dot(upRoom);
        double locZ = vecMY.dot(forwardRoom);

        return originGame
                .add(rightGame.scale(locX / scale))
                .add(upGame.scale(locY / scale))
                .add(forwardGame.scale(locZ / scale));
    }
}
