package it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.init;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.ReliquifiedIronsSpellsAndSpellbooks;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.UUID;

public class RISASDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, ReliquifiedIronsSpellsAndSpellbooks.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<VoodooMarkData>>> BLOODY_VOODOO_DOLL_MARKS = construct(
            "voodoo_doll/marks",
            VoodooMarkData.CODEC.listOf(),
            VoodooMarkData.STREAM_CODEC.apply(ByteBufCodecs.list())
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<VoodooNeedleReleaseData>>> BLOODY_VOODOO_DOLL_RELEASES = construct(
            "voodoo_doll/releases",
            VoodooNeedleReleaseData.CODEC.listOf(),
            VoodooNeedleReleaseData.STREAM_CODEC.apply(ByteBufCodecs.list())
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> cardiac_trap_COOLDOWN_UNTIL = construct(
            "cardiac_trap/cooldown_until",
            Codec.LONG,
            ByteBufCodecs.VAR_LONG
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> cardiac_trap_HEARTSTOP_UNTIL = construct(
            "cardiac_trap/heartstop_until",
            Codec.LONG,
            ByteBufCodecs.VAR_LONG
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> cardiac_trap_PENDING_HEARTSTOP_AT = construct(
            "cardiac_trap/pending_heartstop_at",
            Codec.LONG,
            ByteBufCodecs.VAR_LONG
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> cardiac_trap_PENDING_HEARTSTOP_DURATION = construct(
            "cardiac_trap/pending_heartstop_duration",
            Codec.INT,
            ByteBufCodecs.VAR_INT
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Double>> cardiac_trap_PENDING_HEARTSTOP_DAMAGE = construct(
            "cardiac_trap/pending_heartstop_damage",
            Codec.DOUBLE,
            ByteBufCodecs.DOUBLE
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CloakBufferData>> CLOAK_OF_THE_BLOODY_FEATHER_BUFFER = construct(
            "cloak_of_the_bloody_feather/buffer",
            CloakBufferData.CODEC,
            CloakBufferData.STREAM_CODEC
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> FLASK_OF_THE_RED_MIST_COOLDOWN_UNTIL = construct(
            "flask_of_the_red_mist/cooldown_until",
            Codec.LONG,
            ByteBufCodecs.VAR_LONG
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> FLASK_OF_THE_RED_MIST_BONUS_UNTIL = construct(
            "flask_of_the_red_mist/bonus_until",
            Codec.LONG,
            ByteBufCodecs.VAR_LONG
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<LivingFleshMarkData>>> LIVING_FLESH_MARKS = construct(
            "living_flesh/marks",
            LivingFleshMarkData.CODEC.listOf(),
            LivingFleshMarkData.STREAM_CODEC.apply(ByteBufCodecs.list())
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<LivingFleshScheduledDevourData>>> LIVING_FLESH_SCHEDULED_DEVOURS = construct(
            "living_flesh/scheduled_devours",
            LivingFleshScheduledDevourData.CODEC.listOf(),
            LivingFleshScheduledDevourData.STREAM_CODEC.apply(ByteBufCodecs.list())
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ImmaterialDisperserRetaliationData>>> IMMATERIAL_DISPERSER_RETALIATION = construct(
            "immaterial_disperser/retaliation",
            ImmaterialDisperserRetaliationData.CODEC.listOf(),
            ImmaterialDisperserRetaliationData.STREAM_CODEC.apply(ByteBufCodecs.list())
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<EnderBowMarkData>>> ENDER_BOW_MARKS = construct(
            "ender_bow/marks",
            EnderBowMarkData.CODEC.listOf(),
            EnderBowMarkData.STREAM_CODEC.apply(ByteBufCodecs.list())
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<EchoGloveFallBonusData>>> ECHO_GLOVE_FALL_BONUS = construct(
            "echo_glove/fall_bonus",
            EchoGloveFallBonusData.CODEC.listOf(),
            EchoGloveFallBonusData.STREAM_CODEC.apply(ByteBufCodecs.list())
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<DimensionKeyPointData>> DIMENSION_KEY_POINT = construct(
            "dimension_key/point",
            DimensionKeyPointData.CODEC,
            DimensionKeyPointData.STREAM_CODEC
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<DimensionKeyActivePortalData>> DIMENSION_KEY_ACTIVE_PORTALS = construct(
            "dimension_key/active_portals",
            DimensionKeyActivePortalData.CODEC,
            DimensionKeyActivePortalData.STREAM_CODEC
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<GalaxyDevourerDiademBlackHoleData>>> GALAXY_DEVOURER_DIADEM_BLACK_HOLES = construct(
            "galaxy_devourer_diadem/black_holes",
            GalaxyDevourerDiademBlackHoleData.CODEC.listOf(),
            GalaxyDevourerDiademBlackHoleData.STREAM_CODEC.apply(ByteBufCodecs.list())
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<GalaxyDevourerDiademDamageData>>> GALAXY_DEVOURER_DIADEM_DAMAGE_DEALT = construct(
            "galaxy_devourer_diadem/damage_dealt",
            GalaxyDevourerDiademDamageData.CODEC.listOf(),
            GalaxyDevourerDiademDamageData.STREAM_CODEC.apply(ByteBufCodecs.list())
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<SealedWeaponSummonData>>> SEALED_WEAPON_SUMMONS = construct(
            "sealed_weapon/summons",
            SealedWeaponSummonData.CODEC.listOf(),
            SealedWeaponSummonData.STREAM_CODEC.apply(ByteBufCodecs.list())
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<UUID>> SEALED_WEAPON_INSTANCE = construct(
            "sealed_weapon/instance",
            UUIDUtil.CODEC,
            UUIDUtil.STREAM_CODEC
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> MIRROR_OF_TRANSGRESSION_USE_STARTED_AT = construct(
            "mirror_of_transgression/use_started_at",
            Codec.LONG,
            ByteBufCodecs.VAR_LONG
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> PULSAR_MANTLE_COOLDOWN_UNTIL = construct(
            "pulsar_mantle/cooldown_until",
            Codec.LONG,
            ByteBufCodecs.VAR_LONG
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> PULSAR_MANTLE_PENDING_MISSILES = construct(
            "pulsar_mantle/pending_missiles",
            Codec.INT,
            ByteBufCodecs.VAR_INT
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> PULSAR_MANTLE_NEXT_SHOT_AT = construct(
            "pulsar_mantle/next_shot_at",
            Codec.LONG,
            ByteBufCodecs.VAR_LONG
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> PULSAR_MANTLE_RELEASE_STEP = construct(
            "pulsar_mantle/release_step",
            Codec.INT,
            ByteBufCodecs.VAR_INT
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> PULSAR_MANTLE_BELOW_THRESHOLD = construct(
            "pulsar_mantle/below_threshold",
            Codec.BOOL,
            ByteBufCodecs.BOOL
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> MASK_OF_HUNGER_COOLDOWN_UNTIL = construct(
            "mask_of_hunger/cooldown_until",
            Codec.LONG,
            ByteBufCodecs.VAR_LONG
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Double>> RING_OF_ELUSIVENESS_CHARGES = construct(
            "ring_of_elusiveness/charges",
            Codec.DOUBLE,
            ByteBufCodecs.DOUBLE
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> RING_OF_ELUSIVENESS_LAST_UPDATE = construct(
            "ring_of_elusiveness/last_update",
            Codec.LONG,
            ByteBufCodecs.VAR_LONG
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> RING_OF_ELUSIVENESS_BONUS_UNTIL = construct(
            "ring_of_elusiveness/bonus_until",
            Codec.LONG,
            ByteBufCodecs.VAR_LONG
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Double>> SHADOW_CLAWS_CHARGES = construct(
            "shadow_claws/charges",
            Codec.DOUBLE,
            ByteBufCodecs.DOUBLE
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> SHADOW_CLAWS_LAST_UPDATE = construct(
            "shadow_claws/last_update",
            Codec.LONG,
            ByteBufCodecs.VAR_LONG
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> SHADOW_CLAWS_SHADOW_SLASH_ACTIVE = construct(
            "shadow_claws/shadow_slash_active",
            Codec.BOOL,
            ByteBufCodecs.BOOL
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> SHADOW_CLAWS_SHADOW_SLASH_HIT = construct(
            "shadow_claws/shadow_slash_hit",
            Codec.BOOL,
            ByteBufCodecs.BOOL
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<SinnerCrownSummonData>>> SINNER_CROWN_SUMMONS = construct(
            "sinner_crown/summons",
            SinnerCrownSummonData.CODEC.listOf(),
            SinnerCrownSummonData.STREAM_CODEC.apply(ByteBufCodecs.list())
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SlicerStateData>> SLICER_STATE = construct(
            "slicer/state",
            SlicerStateData.CODEC,
            SlicerStateData.STREAM_CODEC
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> SLICER_COOLDOWN_UNTIL = construct(
            "slicer/cooldown_until",
            Codec.LONG,
            ByteBufCodecs.VAR_LONG
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> SLICER_ACTIVE = construct(
            "slicer/active",
            Codec.BOOL,
            ByteBufCodecs.BOOL
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> SLICER_BLADE_CHARGES = construct(
            "slicer/blade_charges",
            Codec.INT,
            ByteBufCodecs.VAR_INT
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> SLICER_BLADE_WINDOW_UNTIL = construct(
            "slicer/blade_window_until",
            Codec.LONG,
            ByteBufCodecs.VAR_LONG
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> SLICER_BLADE_DECAY_AT = construct(
            "slicer/blade_decay_at",
            Codec.LONG,
            ByteBufCodecs.VAR_LONG
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> SLICER_REVEAL_UNTIL = construct(
            "slicer/reveal_until",
            Codec.LONG,
            ByteBufCodecs.VAR_LONG
    );

    private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> construct(
            String name,
            Codec<T> codec,
            StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec
    ) {
        return DATA_COMPONENTS.register(name, () -> DataComponentType.<T>builder()
                .persistent(codec)
                .networkSynchronized(streamCodec)
                .build());
    }

    public static void register(IEventBus bus) {
        DATA_COMPONENTS.register(bus);
    }

    public record VoodooMarkData(UUID target, ResourceKey<Level> level, UUID caster, long expiresAtTick, double needleDamagePerPoint, double storedDamage) {
        public static final Codec<VoodooMarkData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                UUIDUtil.CODEC.fieldOf("target").forGetter(VoodooMarkData::target),
                Level.RESOURCE_KEY_CODEC.fieldOf("level").forGetter(VoodooMarkData::level),
                UUIDUtil.CODEC.fieldOf("caster").forGetter(VoodooMarkData::caster),
                Codec.LONG.fieldOf("expires_at_tick").forGetter(VoodooMarkData::expiresAtTick),
                Codec.DOUBLE.fieldOf("needle_damage_per_point").forGetter(VoodooMarkData::needleDamagePerPoint),
                Codec.DOUBLE.fieldOf("stored_damage").forGetter(VoodooMarkData::storedDamage)
        ).apply(instance, VoodooMarkData::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, VoodooMarkData> STREAM_CODEC = StreamCodec.composite(
                UUIDUtil.STREAM_CODEC,
                VoodooMarkData::target,
                ResourceKey.streamCodec(Registries.DIMENSION),
                VoodooMarkData::level,
                UUIDUtil.STREAM_CODEC,
                VoodooMarkData::caster,
                ByteBufCodecs.VAR_LONG,
                VoodooMarkData::expiresAtTick,
                ByteBufCodecs.DOUBLE,
                VoodooMarkData::needleDamagePerPoint,
                ByteBufCodecs.DOUBLE,
                VoodooMarkData::storedDamage,
                VoodooMarkData::new
        );

        public VoodooMarkData withAddedStoredDamage(double amount) {
            return new VoodooMarkData(this.target, this.level, this.caster, this.expiresAtTick, this.needleDamagePerPoint, this.storedDamage + Math.max(0D, amount));
        }
    }

    public record VoodooNeedleReleaseData(UUID target, ResourceKey<Level> level, UUID caster, double needleDamage, int remainingNeedles, long nextShotTick) {
        public static final Codec<VoodooNeedleReleaseData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                UUIDUtil.CODEC.fieldOf("target").forGetter(VoodooNeedleReleaseData::target),
                Level.RESOURCE_KEY_CODEC.fieldOf("level").forGetter(VoodooNeedleReleaseData::level),
                UUIDUtil.CODEC.fieldOf("caster").forGetter(VoodooNeedleReleaseData::caster),
                Codec.DOUBLE.fieldOf("needle_damage").forGetter(VoodooNeedleReleaseData::needleDamage),
                Codec.INT.fieldOf("remaining_needles").forGetter(VoodooNeedleReleaseData::remainingNeedles),
                Codec.LONG.fieldOf("next_shot_tick").forGetter(VoodooNeedleReleaseData::nextShotTick)
        ).apply(instance, VoodooNeedleReleaseData::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, VoodooNeedleReleaseData> STREAM_CODEC = StreamCodec.composite(
                UUIDUtil.STREAM_CODEC,
                VoodooNeedleReleaseData::target,
                ResourceKey.streamCodec(Registries.DIMENSION),
                VoodooNeedleReleaseData::level,
                UUIDUtil.STREAM_CODEC,
                VoodooNeedleReleaseData::caster,
                ByteBufCodecs.DOUBLE,
                VoodooNeedleReleaseData::needleDamage,
                ByteBufCodecs.VAR_INT,
                VoodooNeedleReleaseData::remainingNeedles,
                ByteBufCodecs.VAR_LONG,
                VoodooNeedleReleaseData::nextShotTick,
                VoodooNeedleReleaseData::new
        );
    }

    public record CloakBufferData(ResourceKey<Level> level, long lastDamageTick, int pendingNeedles, int releasingNeedles, int currentReleaseIndex, int extraCyclesUsed) {
        public static final Codec<CloakBufferData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Level.RESOURCE_KEY_CODEC.fieldOf("level").forGetter(CloakBufferData::level),
                Codec.LONG.fieldOf("last_damage_tick").forGetter(CloakBufferData::lastDamageTick),
                Codec.INT.fieldOf("pending_needles").forGetter(CloakBufferData::pendingNeedles),
                Codec.INT.fieldOf("releasing_needles").forGetter(CloakBufferData::releasingNeedles),
                Codec.INT.fieldOf("current_release_index").forGetter(CloakBufferData::currentReleaseIndex),
                Codec.INT.optionalFieldOf("extra_cycles_used", 0).forGetter(CloakBufferData::extraCyclesUsed)
        ).apply(instance, CloakBufferData::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, CloakBufferData> STREAM_CODEC = StreamCodec.composite(
                ResourceKey.streamCodec(Registries.DIMENSION),
                CloakBufferData::level,
                ByteBufCodecs.VAR_LONG,
                CloakBufferData::lastDamageTick,
                ByteBufCodecs.VAR_INT,
                CloakBufferData::pendingNeedles,
                ByteBufCodecs.VAR_INT,
                CloakBufferData::releasingNeedles,
                ByteBufCodecs.VAR_INT,
                CloakBufferData::currentReleaseIndex,
                ByteBufCodecs.VAR_INT,
                CloakBufferData::extraCyclesUsed,
                CloakBufferData::new
        );

        public static CloakBufferData empty(ResourceKey<Level> level) {
            return new CloakBufferData(level, 0L, 0, 0, 0, 0);
        }

        public int releaseTotalNeedles() {
            return this.releasingNeedles + this.currentReleaseIndex;
        }

        public boolean isReleasing() {
            return this.releasingNeedles > 0;
        }

        public boolean isIdle() {
            return this.pendingNeedles <= 0 && this.releasingNeedles <= 0;
        }

        public CloakBufferData withAddedPendingNeedles(int needles) {
            return new CloakBufferData(
                    this.level,
                    this.lastDamageTick,
                    Math.max(0, this.pendingNeedles + Math.max(0, needles)),
                    this.releasingNeedles,
                    this.currentReleaseIndex,
                    this.extraCyclesUsed
            );
        }

        public CloakBufferData onDamageTaken(long tick) {
            if (this.releasingNeedles <= 0) {
                return new CloakBufferData(this.level, tick, this.pendingNeedles, this.releasingNeedles, this.currentReleaseIndex, 0);
            }

            return new CloakBufferData(this.level, tick, this.pendingNeedles + this.releasingNeedles, 0, 0, 0);
        }

        public CloakBufferData beginRelease() {
            if (this.releasingNeedles > 0 || this.pendingNeedles <= 0)
                return this;

            return new CloakBufferData(this.level, this.lastDamageTick, 0, this.pendingNeedles, 0, this.extraCyclesUsed);
        }

        public CloakBufferData releaseOne() {
            if (this.releasingNeedles <= 0)
                return this;

            var releasingNeedles = this.releasingNeedles - 1;
            var currentReleaseIndex = this.currentReleaseIndex + 1;

            if (releasingNeedles <= 0)
                return new CloakBufferData(this.level, this.lastDamageTick, this.pendingNeedles, 0, 0, this.extraCyclesUsed);

            return new CloakBufferData(this.level, this.lastDamageTick, this.pendingNeedles, releasingNeedles, currentReleaseIndex, this.extraCyclesUsed);
        }
    }

    public record LivingFleshMarkData(UUID target, ResourceKey<Level> level, UUID caster, long expiresAtTick, double devourDamage) {
        public static final Codec<LivingFleshMarkData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                UUIDUtil.CODEC.fieldOf("target").forGetter(LivingFleshMarkData::target),
                Level.RESOURCE_KEY_CODEC.fieldOf("level").forGetter(LivingFleshMarkData::level),
                UUIDUtil.CODEC.fieldOf("caster").forGetter(LivingFleshMarkData::caster),
                Codec.LONG.fieldOf("expires_at_tick").forGetter(LivingFleshMarkData::expiresAtTick),
                Codec.DOUBLE.fieldOf("devour_damage").forGetter(LivingFleshMarkData::devourDamage)
        ).apply(instance, LivingFleshMarkData::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, LivingFleshMarkData> STREAM_CODEC = StreamCodec.composite(
                UUIDUtil.STREAM_CODEC,
                LivingFleshMarkData::target,
                ResourceKey.streamCodec(Registries.DIMENSION),
                LivingFleshMarkData::level,
                UUIDUtil.STREAM_CODEC,
                LivingFleshMarkData::caster,
                ByteBufCodecs.VAR_LONG,
                LivingFleshMarkData::expiresAtTick,
                ByteBufCodecs.DOUBLE,
                LivingFleshMarkData::devourDamage,
                LivingFleshMarkData::new
        );
    }

    public record LivingFleshScheduledDevourPayload(double devourDamage, double food, double saturation, double heal) {
        public static final Codec<LivingFleshScheduledDevourPayload> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.DOUBLE.fieldOf("devour_damage").forGetter(LivingFleshScheduledDevourPayload::devourDamage),
                Codec.DOUBLE.fieldOf("food").forGetter(LivingFleshScheduledDevourPayload::food),
                Codec.DOUBLE.fieldOf("saturation").forGetter(LivingFleshScheduledDevourPayload::saturation),
                Codec.DOUBLE.fieldOf("heal").forGetter(LivingFleshScheduledDevourPayload::heal)
        ).apply(instance, LivingFleshScheduledDevourPayload::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, LivingFleshScheduledDevourPayload> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.DOUBLE,
                LivingFleshScheduledDevourPayload::devourDamage,
                ByteBufCodecs.DOUBLE,
                LivingFleshScheduledDevourPayload::food,
                ByteBufCodecs.DOUBLE,
                LivingFleshScheduledDevourPayload::saturation,
                ByteBufCodecs.DOUBLE,
                LivingFleshScheduledDevourPayload::heal,
                LivingFleshScheduledDevourPayload::new
        );
    }

    public record LivingFleshScheduledDevourData(UUID target, ResourceKey<Level> level, UUID caster, long triggerAtTick, LivingFleshScheduledDevourPayload payload) {
        public static final Codec<LivingFleshScheduledDevourData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                UUIDUtil.CODEC.fieldOf("target").forGetter(LivingFleshScheduledDevourData::target),
                Level.RESOURCE_KEY_CODEC.fieldOf("level").forGetter(LivingFleshScheduledDevourData::level),
                UUIDUtil.CODEC.fieldOf("caster").forGetter(LivingFleshScheduledDevourData::caster),
                Codec.LONG.fieldOf("trigger_at_tick").forGetter(LivingFleshScheduledDevourData::triggerAtTick),
                LivingFleshScheduledDevourPayload.CODEC.fieldOf("payload").forGetter(LivingFleshScheduledDevourData::payload)
        ).apply(instance, LivingFleshScheduledDevourData::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, LivingFleshScheduledDevourData> STREAM_CODEC = StreamCodec.composite(
                UUIDUtil.STREAM_CODEC,
                LivingFleshScheduledDevourData::target,
                ResourceKey.streamCodec(Registries.DIMENSION),
                LivingFleshScheduledDevourData::level,
                UUIDUtil.STREAM_CODEC,
                LivingFleshScheduledDevourData::caster,
                ByteBufCodecs.VAR_LONG,
                LivingFleshScheduledDevourData::triggerAtTick,
                LivingFleshScheduledDevourPayload.STREAM_CODEC,
                LivingFleshScheduledDevourData::payload,
                LivingFleshScheduledDevourData::new
        );
    }

    public record ImmaterialDisperserRetaliationData(UUID target, ResourceKey<Level> level, long expiresAtTick, double bonus) {
        public static final Codec<ImmaterialDisperserRetaliationData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                UUIDUtil.CODEC.fieldOf("target").forGetter(ImmaterialDisperserRetaliationData::target),
                Level.RESOURCE_KEY_CODEC.fieldOf("level").forGetter(ImmaterialDisperserRetaliationData::level),
                Codec.LONG.fieldOf("expires_at_tick").forGetter(ImmaterialDisperserRetaliationData::expiresAtTick),
                Codec.DOUBLE.fieldOf("bonus").forGetter(ImmaterialDisperserRetaliationData::bonus)
        ).apply(instance, ImmaterialDisperserRetaliationData::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ImmaterialDisperserRetaliationData> STREAM_CODEC = StreamCodec.composite(
                UUIDUtil.STREAM_CODEC,
                ImmaterialDisperserRetaliationData::target,
                ResourceKey.streamCodec(Registries.DIMENSION),
                ImmaterialDisperserRetaliationData::level,
                ByteBufCodecs.VAR_LONG,
                ImmaterialDisperserRetaliationData::expiresAtTick,
                ByteBufCodecs.DOUBLE,
                ImmaterialDisperserRetaliationData::bonus,
                ImmaterialDisperserRetaliationData::new
        );
    }

    public record EnderBowMarkData(UUID target, ResourceKey<Level> level, long expiresAtTick) {
        public static final Codec<EnderBowMarkData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                UUIDUtil.CODEC.fieldOf("target").forGetter(EnderBowMarkData::target),
                Level.RESOURCE_KEY_CODEC.fieldOf("level").forGetter(EnderBowMarkData::level),
                Codec.LONG.fieldOf("expires_at_tick").forGetter(EnderBowMarkData::expiresAtTick)
        ).apply(instance, EnderBowMarkData::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, EnderBowMarkData> STREAM_CODEC = StreamCodec.composite(
                UUIDUtil.STREAM_CODEC,
                EnderBowMarkData::target,
                ResourceKey.streamCodec(Registries.DIMENSION),
                EnderBowMarkData::level,
                ByteBufCodecs.VAR_LONG,
                EnderBowMarkData::expiresAtTick,
                EnderBowMarkData::new
        );
    }

    public record EchoGloveFallBonusData(UUID target, ResourceKey<Level> level, long expiresAtTick, double bonus) {
        public static final Codec<EchoGloveFallBonusData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                UUIDUtil.CODEC.fieldOf("target").forGetter(EchoGloveFallBonusData::target),
                Level.RESOURCE_KEY_CODEC.fieldOf("level").forGetter(EchoGloveFallBonusData::level),
                Codec.LONG.fieldOf("expires_at_tick").forGetter(EchoGloveFallBonusData::expiresAtTick),
                Codec.DOUBLE.fieldOf("bonus").forGetter(EchoGloveFallBonusData::bonus)
        ).apply(instance, EchoGloveFallBonusData::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, EchoGloveFallBonusData> STREAM_CODEC = StreamCodec.composite(
                UUIDUtil.STREAM_CODEC,
                EchoGloveFallBonusData::target,
                ResourceKey.streamCodec(Registries.DIMENSION),
                EchoGloveFallBonusData::level,
                ByteBufCodecs.VAR_LONG,
                EchoGloveFallBonusData::expiresAtTick,
                ByteBufCodecs.DOUBLE,
                EchoGloveFallBonusData::bonus,
                EchoGloveFallBonusData::new
        );
    }

    public record DimensionKeyPointData(ResourceKey<Level> level, double x, double y, double z, float yRot) {
        public static final Codec<DimensionKeyPointData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Level.RESOURCE_KEY_CODEC.fieldOf("level").forGetter(DimensionKeyPointData::level),
                Codec.DOUBLE.fieldOf("x").forGetter(DimensionKeyPointData::x),
                Codec.DOUBLE.fieldOf("y").forGetter(DimensionKeyPointData::y),
                Codec.DOUBLE.fieldOf("z").forGetter(DimensionKeyPointData::z),
                Codec.FLOAT.fieldOf("y_rot").forGetter(DimensionKeyPointData::yRot)
        ).apply(instance, DimensionKeyPointData::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, DimensionKeyPointData> STREAM_CODEC = StreamCodec.composite(
                ResourceKey.streamCodec(Registries.DIMENSION),
                DimensionKeyPointData::level,
                ByteBufCodecs.DOUBLE,
                DimensionKeyPointData::x,
                ByteBufCodecs.DOUBLE,
                DimensionKeyPointData::y,
                ByteBufCodecs.DOUBLE,
                DimensionKeyPointData::z,
                ByteBufCodecs.FLOAT,
                DimensionKeyPointData::yRot,
                DimensionKeyPointData::new
        );
    }

    public record DimensionKeyActivePortalData(
            ResourceKey<Level> firstLevel,
            UUID firstPortal,
            ResourceKey<Level> secondLevel,
            UUID secondPortal,
            long expiresAtTick,
            List<UUID> processedEntities
    ) {
        public static final Codec<DimensionKeyActivePortalData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Level.RESOURCE_KEY_CODEC.fieldOf("first_level").forGetter(DimensionKeyActivePortalData::firstLevel),
                UUIDUtil.CODEC.fieldOf("first_portal").forGetter(DimensionKeyActivePortalData::firstPortal),
                Level.RESOURCE_KEY_CODEC.fieldOf("second_level").forGetter(DimensionKeyActivePortalData::secondLevel),
                UUIDUtil.CODEC.fieldOf("second_portal").forGetter(DimensionKeyActivePortalData::secondPortal),
                Codec.LONG.fieldOf("expires_at_tick").forGetter(DimensionKeyActivePortalData::expiresAtTick),
                UUIDUtil.CODEC.listOf().optionalFieldOf("processed_entities", List.of()).forGetter(DimensionKeyActivePortalData::processedEntities)
        ).apply(instance, DimensionKeyActivePortalData::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, DimensionKeyActivePortalData> STREAM_CODEC = StreamCodec.composite(
                ResourceKey.streamCodec(Registries.DIMENSION),
                DimensionKeyActivePortalData::firstLevel,
                UUIDUtil.STREAM_CODEC,
                DimensionKeyActivePortalData::firstPortal,
                ResourceKey.streamCodec(Registries.DIMENSION),
                DimensionKeyActivePortalData::secondLevel,
                UUIDUtil.STREAM_CODEC,
                DimensionKeyActivePortalData::secondPortal,
                ByteBufCodecs.VAR_LONG,
                DimensionKeyActivePortalData::expiresAtTick,
                UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs.list()),
                DimensionKeyActivePortalData::processedEntities,
                DimensionKeyActivePortalData::new
        );
    }

    public record GalaxyDevourerDiademBlackHoleData(UUID blackHole, ResourceKey<Level> level, double x, double y, double z, long expiresAtTick) {
        public static final Codec<GalaxyDevourerDiademBlackHoleData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                UUIDUtil.CODEC.fieldOf("black_hole").forGetter(GalaxyDevourerDiademBlackHoleData::blackHole),
                Level.RESOURCE_KEY_CODEC.fieldOf("level").forGetter(GalaxyDevourerDiademBlackHoleData::level),
                Codec.DOUBLE.fieldOf("x").forGetter(GalaxyDevourerDiademBlackHoleData::x),
                Codec.DOUBLE.fieldOf("y").forGetter(GalaxyDevourerDiademBlackHoleData::y),
                Codec.DOUBLE.fieldOf("z").forGetter(GalaxyDevourerDiademBlackHoleData::z),
                Codec.LONG.fieldOf("expires_at_tick").forGetter(GalaxyDevourerDiademBlackHoleData::expiresAtTick)
        ).apply(instance, GalaxyDevourerDiademBlackHoleData::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, GalaxyDevourerDiademBlackHoleData> STREAM_CODEC = StreamCodec.composite(
                UUIDUtil.STREAM_CODEC,
                GalaxyDevourerDiademBlackHoleData::blackHole,
                ResourceKey.streamCodec(Registries.DIMENSION),
                GalaxyDevourerDiademBlackHoleData::level,
                ByteBufCodecs.DOUBLE,
                GalaxyDevourerDiademBlackHoleData::x,
                ByteBufCodecs.DOUBLE,
                GalaxyDevourerDiademBlackHoleData::y,
                ByteBufCodecs.DOUBLE,
                GalaxyDevourerDiademBlackHoleData::z,
                ByteBufCodecs.VAR_LONG,
                GalaxyDevourerDiademBlackHoleData::expiresAtTick,
                GalaxyDevourerDiademBlackHoleData::new
        );
    }

    public record GalaxyDevourerDiademDamageData(UUID blackHole, ResourceKey<Level> level, double dealtDamage) {
        public static final Codec<GalaxyDevourerDiademDamageData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                UUIDUtil.CODEC.fieldOf("black_hole").forGetter(GalaxyDevourerDiademDamageData::blackHole),
                Level.RESOURCE_KEY_CODEC.fieldOf("level").forGetter(GalaxyDevourerDiademDamageData::level),
                Codec.DOUBLE.fieldOf("dealt_damage").forGetter(GalaxyDevourerDiademDamageData::dealtDamage)
        ).apply(instance, GalaxyDevourerDiademDamageData::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, GalaxyDevourerDiademDamageData> STREAM_CODEC = StreamCodec.composite(
                UUIDUtil.STREAM_CODEC,
                GalaxyDevourerDiademDamageData::blackHole,
                ResourceKey.streamCodec(Registries.DIMENSION),
                GalaxyDevourerDiademDamageData::level,
                ByteBufCodecs.DOUBLE,
                GalaxyDevourerDiademDamageData::dealtDamage,
                GalaxyDevourerDiademDamageData::new
        );
    }

    public record SealedWeaponSummonData(int weaponType, UUID summon, ResourceKey<Level> level, long respawnAtTick) {
        public static final Codec<SealedWeaponSummonData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("weapon_type").forGetter(SealedWeaponSummonData::weaponType),
                UUIDUtil.CODEC.fieldOf("summon").forGetter(SealedWeaponSummonData::summon),
                Level.RESOURCE_KEY_CODEC.fieldOf("level").forGetter(SealedWeaponSummonData::level),
                Codec.LONG.fieldOf("respawn_at_tick").forGetter(SealedWeaponSummonData::respawnAtTick)
        ).apply(instance, SealedWeaponSummonData::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, SealedWeaponSummonData> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT,
                SealedWeaponSummonData::weaponType,
                UUIDUtil.STREAM_CODEC,
                SealedWeaponSummonData::summon,
                ResourceKey.streamCodec(Registries.DIMENSION),
                SealedWeaponSummonData::level,
                ByteBufCodecs.VAR_LONG,
                SealedWeaponSummonData::respawnAtTick,
                SealedWeaponSummonData::new
        );
    }

    public record SinnerCrownSummonData(UUID summon, ResourceKey<Level> level, long expiresAtTick, double explosionDamage) {
        public static final Codec<SinnerCrownSummonData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                UUIDUtil.CODEC.fieldOf("summon").forGetter(SinnerCrownSummonData::summon),
                Level.RESOURCE_KEY_CODEC.fieldOf("level").forGetter(SinnerCrownSummonData::level),
                Codec.LONG.fieldOf("expires_at_tick").forGetter(SinnerCrownSummonData::expiresAtTick),
                Codec.DOUBLE.fieldOf("explosion_damage").forGetter(SinnerCrownSummonData::explosionDamage)
        ).apply(instance, SinnerCrownSummonData::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, SinnerCrownSummonData> STREAM_CODEC = StreamCodec.composite(
                UUIDUtil.STREAM_CODEC,
                SinnerCrownSummonData::summon,
                ResourceKey.streamCodec(Registries.DIMENSION),
                SinnerCrownSummonData::level,
                ByteBufCodecs.VAR_LONG,
                SinnerCrownSummonData::expiresAtTick,
                ByteBufCodecs.DOUBLE,
                SinnerCrownSummonData::explosionDamage,
                SinnerCrownSummonData::new
        );
    }

    public record SlicerStateData(ResourceKey<Level> level, double centerX, double centerY, double centerZ, double radius, long nextActionTick, int intervalTicks, float initialYaw, float initialPitch, List<UUID> attackedTargets) {
        public static final Codec<SlicerStateData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Level.RESOURCE_KEY_CODEC.fieldOf("level").forGetter(SlicerStateData::level),
                Codec.DOUBLE.fieldOf("center_x").forGetter(SlicerStateData::centerX),
                Codec.DOUBLE.fieldOf("center_y").forGetter(SlicerStateData::centerY),
                Codec.DOUBLE.fieldOf("center_z").forGetter(SlicerStateData::centerZ),
                Codec.DOUBLE.fieldOf("radius").forGetter(SlicerStateData::radius),
                Codec.LONG.fieldOf("next_action_tick").forGetter(SlicerStateData::nextActionTick),
                Codec.INT.fieldOf("interval_ticks").forGetter(SlicerStateData::intervalTicks),
                Codec.FLOAT.optionalFieldOf("initial_yaw", 0F).forGetter(SlicerStateData::initialYaw),
                Codec.FLOAT.optionalFieldOf("initial_pitch", 0F).forGetter(SlicerStateData::initialPitch),
                UUIDUtil.CODEC.listOf().fieldOf("attacked_targets").forGetter(SlicerStateData::attackedTargets)
        ).apply(instance, SlicerStateData::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, SlicerStateData> STREAM_CODEC = new StreamCodec<>() {
            @Override
            public SlicerStateData decode(RegistryFriendlyByteBuf buffer) {
                return new SlicerStateData(
                        ResourceKey.streamCodec(Registries.DIMENSION).decode(buffer),
                        ByteBufCodecs.DOUBLE.decode(buffer),
                        ByteBufCodecs.DOUBLE.decode(buffer),
                        ByteBufCodecs.DOUBLE.decode(buffer),
                        ByteBufCodecs.DOUBLE.decode(buffer),
                        ByteBufCodecs.VAR_LONG.decode(buffer),
                        ByteBufCodecs.VAR_INT.decode(buffer),
                        ByteBufCodecs.FLOAT.decode(buffer),
                        ByteBufCodecs.FLOAT.decode(buffer),
                        UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buffer)
                );
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buffer, SlicerStateData value) {
                ResourceKey.streamCodec(Registries.DIMENSION).encode(buffer, value.level());
                ByteBufCodecs.DOUBLE.encode(buffer, value.centerX());
                ByteBufCodecs.DOUBLE.encode(buffer, value.centerY());
                ByteBufCodecs.DOUBLE.encode(buffer, value.centerZ());
                ByteBufCodecs.DOUBLE.encode(buffer, value.radius());
                ByteBufCodecs.VAR_LONG.encode(buffer, value.nextActionTick());
                ByteBufCodecs.VAR_INT.encode(buffer, value.intervalTicks());
                ByteBufCodecs.FLOAT.encode(buffer, value.initialYaw());
                ByteBufCodecs.FLOAT.encode(buffer, value.initialPitch());
                UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buffer, value.attackedTargets());
            }
        };
    }
}
