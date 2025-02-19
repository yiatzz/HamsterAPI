package dev._2lstudios.hamsterapi.enums;

public enum PacketType {
    // Out packets
    PacketPlayOutAbilities, PacketPlayOutAnimation, PacketPlayOutAttachEntity, PlayPacketOutBed,
    PacketPlayOutBlockAction, PacketPlayOutBlockBreakAnimation, PacketPlayOutBlockChange, PacketPlayOutChat,
    PacketPlayOutCloseWindow, PacketPlayOutCollect, PacketPlayOutCustomPayload, PacketPlayOutEntity,
    PacketPlayOutEntityDestroy, PacketPlayOutEntityEffect, PacketPlayOutEntityEquipment,
    PacketPlayOutEntityHeadRotation, PacketPlayOutEntityLook, PacketPlayOutEntityMetadata, PacketPlayOutEntityStatus,
    PacketPlayOutEntityTeleport, PacketPlayOutEntityVelocity, PacketPlayOutExperience, PacketPlayOutExplosion,
    PacketPlayOutGameStateChange, PacketPlayOutHeldItemSlot, PacketPlayOutKeepAlive, PacketPlayOutKickDisconnect,
    PacketPlayOutListener, PacketPlayOutLogin, PacketPlayOutMap, PacketPlayOutMapChunk, PacketPlayOutMapChunkBulk,
    PacketPlayOutMultiBlockChange, PacketPlayOutNamedEntitySpawn, PacketPlayOutNamedSoundEffect,
    PacketPlayOutOpenSignEditor, PacketPlayOutOpenWindow, PacketPlayOutPlayerInfo, PacketPlayOutPosition,
    PacketPlayOutRelEntityMove, PacketPlayOutRelEntityMoveLook, PacketPlayOutRemoveEntityEffect, PacketPlayOutRespawn,
    PacketPlayOutScoreboardDisplayObjective, PacketPlayOutScoreboardObjective, PacketPlayOutScoreboardScore,
    PacketPlayOutScoreboardTeam, PacketPlayOutSetSlot, PacketPlayOutSpawnEntity, PacketPlayOutSpawnEntityExperienceOrb,
    PacketPlayOutSpawnEntityLiving, PacketPlayOutSpawnEntityPainting, PacketPlayOutSpawnEntityWeather,
    PacketPlayOutSpawnPosition, PacketPlayOutStatistic, PacketPlayOutTabComplete, PacketPlayOutTileEntityData,
    PacketPlayOutTransaction, PacketPlayOutUpdateAttributes, PacketPlayOutUpdateHealth, PacketPlayOutUpdateSign,
    PacketPlayOutUpdateTime, PacketPlayOutWindowData, PacketPlayOutWindowItems, PacketPlayOutWorldEven,
    PacketPlayOutWorldParticles,

    // In packets
    PacketPlayInAbilities, PacketPlayInArmAnimation, PacketPlayInBlockDig, PacketPlayInBlockPlace, PacketPlayInChat,
    PacketPlayInClientCommand, PacketPlayInCloseWindow, PacketPlayInCustomPayload, PacketPlayInEnchantItem,
    PacketPlayInEntityAction, PacketPlayInFlying, PacketPlayInHeldItemSlot, PacketPlayInKeepAlive, PacketPlayInLook,
    PacketPlayInPosition, PacketPlayInPositionLook, PacketPlayInSetCreativeSlot, PacketPlayInSettings,
    PacketPlayInSteerVehicle, PacketPlayInTabComplete, PacketPlayInTransaction, PacketPlayInUpdateSign,
    PacketPlayInUseEntity, PacketPlayInWindowClick, PacketPlayInAutoRecipe, PacketPlayInRecipeDisplayed, PacketPlayInBoatMove,
    PacketPlayInAdvancements, PacketPlayInResourcePackStatus, PacketPlayInUseItem, PacketPlayInTrSel, PacketPlayInTileNBTQuery,
    PacketPlayInTeleportAccept, PacketPlayInStruct, PacketPlayInSpectate, PacketPlayInSetJigsaw, PacketPlayInCommandBlock,
    PacketPlayInCommandMinecart, PacketPlayInRecipeSettings, PacketPlayInPickItem, PacketPlayInJigsawGenerate, PacketPlayInItemName,
    PacketPlayInDifficultyChange, PacketPlayInDifficultyLock;
}
