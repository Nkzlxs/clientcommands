package net.earthcomputer.clientcommands.command;

import io.netty.buffer.Unpooled;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.earthcomputer.clientcommands.EventManager;
import net.earthcomputer.clientcommands.EventManager.Listener;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;


import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.Arrays;
import java.io.IOException;

public class CommandBook extends ClientCommandBase {


    @Override
    public String getName() {
        return "cbook";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.cbook.usage";
    }

    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0)
            throw new WrongUsageException(getUsage(sender));

        if (!(sender instanceof EntityPlayerSP))
            throw new CommandException("commands.cbook.noPlayer");

        int limit = args.length > 1 ? parseInt(args[1], 1, 50) : 50;
        Random rand = args.length > 2 ? new Random(parseLong(args[2])) : new Random();

        EntityPlayerSP player = (EntityPlayerSP) sender;
        ItemStack heldItem = player.getHeldItemMainhand();
        if (heldItem.getItem() != Items.WRITABLE_BOOK) {
            throw new CommandException("commands.cbook.noBook");
        }

        IntStream characterGenerator;

        switch (args[0]) {
            case "fill":
                characterGenerator = IntStream.generate(() -> 0x10ffff);
                break;
            case "random":
                characterGenerator = rand.ints(0x80, 0x10ffff - 0x800).map(i -> i < 0xd800 ? i : i + 0x800);
                break;
            case "ascii":
                characterGenerator = rand.ints(0x20, 0x7f);
                break;
            case "chineseCharacter":
                Nani naniObj = new Nani(server,sender,args);
                EventManager.addTickListener(new Listener<ClientTickEvent>() {
                    @Override
                    public void accept(ClientTickEvent e) {
                        try {
                            naniObj.increaseTick();
                        } catch (CommandException err) {
                            sender.sendMessage(new TextComponentTranslation("Command Exception occured! "));
                            naniObj.doBook = false;
                        }
                    }

                    @Override
                    public boolean wasFinalAction() {
                        return !naniObj.doBook;
                    }
                });

                return;
            case "randomNkzlxs":
                RandomNkzlxs randNkzObj = new RandomNkzlxs(server,sender,args);
                EventManager.addTickListener(new Listener<ClientTickEvent>() {
                    @Override
                    public void accept(ClientTickEvent e) {
                        try {
                            randNkzObj.increaseTick();
                        } catch (CommandException err) {
                            sender.sendMessage(new TextComponentTranslation("Command Exception occured! "));
                            randNkzObj.doBook = false;
                        }
                    }

                    @Override
                    public boolean wasFinalAction() {
                        return !randNkzObj.doBook;
                    }
                });
                return;
            default:
                throw new CommandException(getUsage(sender));
        }

        String joinedPages = characterGenerator.limit(50 * 210).mapToObj(i -> String.valueOf((char) i)).collect(Collectors.joining());

        NBTTagList pages = new NBTTagList();

        for (int page = 0; page < limit; page++) {
            pages.appendTag(new NBTTagString(joinedPages.substring(page * 210, (page + 1) * 210)));
        }

        if (heldItem.hasTagCompound()) {
            heldItem.getTagCompound().setTag("pages", pages);
        } else {
            heldItem.setTagInfo("pages", pages);
        }
        PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
        buf.writeItemStack(heldItem);
        player.connection.sendPacket(new CPacketCustomPayload("MC|BEdit", buf));

        sender.sendMessage(new TextComponentTranslation("commands.cbook.success"));
    }

        
    // try
    // {
    //     Runtime.getRuntime().exec("/usr/bin/gnome-terminal -e \"echo 'adsf'; exec bash\"");
    // }
    // catch(IOException ioe){
    //     sender.sendMessage(new TextComponentTranslation("nkzlxs.teststrings"));
    // }

    public static String shuffleString(String string)
    {
        List<String> letters = Arrays.asList(string.split(""));
        Collections.shuffle(letters);
        String shuffled = "";
        for (String letter : letters) {
            shuffled += letter;
        }
        return shuffled;
    }

    private static class Nani{
        public boolean doBook = false;
        
        
        private int tickGap = 10;
        private int tickCounter = 0;
        private int d = 0;
        private int inventorySize = 9;
        private ICommandSender mSender = null;
        private String[] mArgs;
        private Random randObj;


        Minecraft MC = FMLClientHandler.instance().getClient();
        

        public Nani(MinecraftServer server, ICommandSender sender, String[] args){
            this.doBook = true; 
            this.mSender = sender;
            this.mArgs = args;
            this.randObj = new Random();
            sender.sendMessage(new TextComponentTranslation("Nani() initiated!"));
        }

        public void increaseTick() throws CommandException{
            if(this.doBook){
                this.tickCounter += 1;
                MC.player.inventory.currentItem = this.d;
                if(this.tickCounter % this.tickGap == 0){

                    this.mSender.sendMessage(new TextComponentTranslation(String.format("tickCounter = %d",this.tickCounter)));
                        
                    if (!(this.mSender instanceof EntityPlayerSP))
                        throw new CommandException("commands.cbook.noPlayer");
    
                    int limit = this.mArgs.length > 1 ? parseInt(this.mArgs[1], 1, 50) : 50;
    
                    EntityPlayerSP player = (EntityPlayerSP) this.mSender;
    
                    ItemStack heldItem = player.inventory.getStackInSlot(this.d);
                    if (heldItem.getItem() != Items.WRITABLE_BOOK) {
                        throw new CommandException("commands.cbook.noBook");
                    }
    
                    String abc = "的离开哦哦我看到神色是了看电视是了可否啊士大夫了看见方式等手榴弹会计法筽额考虑支持看十六大科技放弃感觉本来是的方法是空間離開健康是啦好的是考慮的了客戶老板的了確認他哦看第六次刻錄機案例的空間方法惡搞我是的啦啦隊控制參加我了瘋狂去年你辛苦全額案例覺得放棄來看看就你们没保健哈哈厉害牛逼牛比屁股神奇可以陪子杯子电话手机电脑计算机唐朝汤匙我家住在北区号楼楼前一大片地地上种满了树有几棵是桂花树每到八月份那黄豆大小的淡黄的娇小的花开了散发出阵阵醉人娥幽香我时不时总要摘一些带回家桂花可以做香包桂花糕香水用处可多了不要瞧不起小小的桂花它不去与春天的春花争艳因为它没有美丽的颜色吗它不去与夏日争斗因为它怕热吗它不去与腊梅傲雪战寒因为它怕冷吗它为什么要在秋天开它怕众花嘲笑吗不不是的这是因为它不爱与众花比美在众花作文之中桂花在我心中最美不是因为它用处多不是因为它娇小玲珑招人喜爱是因为它像谦虚的少女不要人夸不要人赞只是为人们撒下绿阴献出芳香供人食用多么有爱心的桂花多么无私的桂花我请大家不要再砍树了也许有一天你砍倒的是一棵棵从前你爱的桂花树一棵棵无私奉献的桂花树那时桂花树可能还再为我们默默奉献希望我们留下它一棵棵桂花树一朵朵娇小的桂花里面含着多少情多少爱多少梦呀幽幽桂花香幽幽桂花情在七十多年前那是时我们的爷爷奶奶还未出世我们的祖国却面临着严重的危机那时人们的耳鼓前充满着炮弹的声音眼眸中浸满了血泪的颜色战争的残酷肆虐着大地也让人们的心灵遭受着重创虽然那些不堪入目的痕迹如今已消失了大半但那些惨痛的记忆从不该被忘记我们生活在和平年代战争没有记忆所以并不能明白战争的残酷有人甚至希望能再出现世界大战让现代高科技武器在战争中大显神威可战争怎会像想象中的那样简单伊拉克战争能让世界为之停顿世界大战就足够让现在的地球毁灭和平不是要像古代人争王夺霸由一人一国统治世界而是要各个不同的国家彼此信赖让世界成为一个共同的家而科技是为了让所有人的生活更加美好不是把人类推向无尽的深渊武器也只是用来消除一些不安全的因素无法切身体会也许真的不容易让人醒悟那就从小见大吧我们身边会有犯罪的出现我们都知道杀人要判死刑也知道在这种情况下死一个人很有可能会害了两个家庭那战争呢死伤无数不经意间会牵动一个国家的生死放大一些中东石油之争可以把多年积攒的财富和幸福在短短几个月就打得支离破碎和平的不易并不深奥只有一句话几百年的和平可以被一次战争就吞的一干二净和平需要被维护哪怕世界不再进步也别让一些不良的进步毁了世界所以我们需要了解需要明白自己的所作所为不仅会和自己有关更会影响到身边的一切由小到大就像曾经人们说的一只巴西的蝴蝶轻扇翅膀就有可能引发美国的一场龙卷风所以我们要学习要读书从书中获得的知识是次要的道德观念人品思想才是主要否则你未来辉煌的成就永远得不到世人的认可没有人知道未来也没有人能改变过去人只有把握好现在改变不了时间那就控制好自己铭记着过去要相信自己我有能力振兴未来丹阳市里庄初级中学还记得很小的时候大人给我讲了许多抗日战争中牺牲的英雄人物的事迹他们有的是久经战场的老兵有的是刚入伍的新兵有的却还是孩子他们不怕牺牲敢于为国家献身的精神便在我们的心里扎下了根随着我慢慢长大和对历史的深入学习与了解我终于明白了抗日战争这一具有历史伟大意义的神圣战争对世界带来的影响七八十年前卢沟桥的枪声揭开了中国人民反击日本法西斯侵略全名抗战的序幕历时八年的铮铮岁月中国人民终于熄灭了日寇在华夏大地烧起的战火遥想当年祖国那积贫积弱的时代日本帝国主义的铁蹄肆意践踏了我国的大好河山置我同胞于水深火热之中对中国人民犯下了令人发指的滔天罪行一边是战争狂人的阴森狞笑一边是国土沦丧血流成河处处燃烧着战火人民家破人亡在南京大屠杀时敌人烧杀抢掠无恶不作对手无寸铁的南京人民和投降的中国军人进行了残忍的屠杀致使三十多万同胞含冤而死今天我们纪念这场伟大战争的胜利就是铭记历史铭记惨遭日寇屠杀的中国人民铭记在抗战中英勇献身的战士铭记参加抗战为战争做出贡献的爱国人士海外华侨国际友人铭记他们的浴血奋战铭记他们的智慧与聪明铭记他们的大公无私铭记他们的深深爱国情我们纪念这场战争不是为了仇恨而是着眼未来历史是不能忘记的忘记意味着背叛这段历史本应渗入我们的血脉合成在我们的中成为子孙后代与生俱来的记忆就像一个永远醒着的伤口以一种永远无法回避的痛楚时时提醒我们落后就要挨打中华当自强回顾中国人民抗战史实和整个中国近现代史放眼当今世界现实我们更深切地认识到只有国家的统一中华民族才能屹立于世界民族之林习近平总书记说每个人的前途命运都与国家和民族的前途命运紧密相连国家好民族好大家才会好实现中华民族伟大复兴是一项光荣而艰巨的事业需要一代又一代中国人共同为之努力知耻而后勇牢记历史勿忘国耻才能从内心深处激发爱国之情和报国之志今天的我们在深深的被那些英灵们所触动的同时更是要居安思危饮水思源牢记历史发奋图强努力学习为中华民族的伟大复兴而努力奋斗遗忘历史是可怕的忘记历史的民族没有历史——题记又是一年九月了听耳畔又响起了激昂的国歌声回眸一看那面鲜红的五星红旗再次冉冉升起弹指一挥间已经年了年前的中国你可曾还记得随着年前卢沟桥上的枪声抗战的序幕被揭开了随着一声声的呐喊中国人民站起来反抗了历时八年的峥嵘岁月中国人民终于熄灭日寇在华夏大地上燃起的战火硝烟中华民族终于扭转了任人宰割的历史古老的中国终于走向独立和自强了遥想当年你可曾还记得日本帝国士兵那冷气阴森的狞笑你可曾记得日本用那飞机炮弹肆意的摧残着我国的大好河山作文你可曾记得日本士兵们丧心病狂的杀戮抢掠你可曾记得南京大屠杀时三十万同胞的含冤而死中国人名生活在水深火热之中街头上是堆积如山的尸体处处可听见啼哭声咆哮声和撕心裂肺的呐喊声就在这关乎中华民族存亡的非常时刻一大批的热血儿女站出来了他们用自己的身躯筑起了坚强的肉盾用自己的鲜血换来了希望与未来如王二小故事原型闫富华将生的希望给了伙伴将死的危险留给了自己似左权在小家与大家之间他毅然决然地选择了大家为了集体显出了自己年轻的生命殊不知在另一方的妻子与女儿是多么的思念他我们要铭记在这些在抗日战争中逝去的英勇的先烈们我们要铭记在灾难时勇敢挺身而出的英豪们我们要铭记在危险时刻义无反顾站出来保护中国人民的海外侨胞们我们要铭记他们的智慧铭记他们的无私铭记他们的勇敢铭记这浓浓的爱国之情时过境迁现在沉睡的雄狮早已觉醒我们正大步地走在世界的前列但日本的投降并不代表着放弃只是短暂的休克所以我们只有不断在教训经验中前进不断进取不断发展不断进步才能不受欺辱不被挨打如今已步入了世纪我们将是未来的接班人我们要自强不息把握历史赋予的使命在祖国统一大业民族伟大复兴的征程中走向祖国激昂时代的最强音在这里让我们宣誓我们是英雄的子孙未来是属于我们的时代学习英雄的精神结果英雄前辈的旗帜从现在起坚定立志把祖国的强盛当做自己的梦想少年们勿忘国耻铭记中华民族伟大的复兴梦让我们朝着未来出发吧我爸爸和妈妈构成了一个美好的家看着咱一家的合影我开心的笑了这是多么温馨融洽幸福的时光啊妈妈把我变得活泼开朗带着我玩儿陪着我疯只不过就是有点天然呆我问你老公拍扇的谜底是什么我问妈妈妈妈下意识愣住了露出一副思索的表情说道这个嘛得让我想想想了半天妈妈也始终没给出答案不就是凄凉吗我终于看不下去了气冲冲说出了答案哦对对我正这么想妈妈说听完这话我瞬间呆滞那我问你古时候有名的天字有哪些呢妈妈想找个台阶下了尽快脱离这尴尬的场面有尧啊舜啊等等我对答入流满脸得瑟我再问你他们做天子都干了什么有名的事啊大禹作文治水妈妈满脸自信目光炯炯望着我哈哈大褥治水哈哈我捧腹大笑对大禹是大禹治水啊妈妈急得面红耳赤连忙说道——这是跳进黄河也洗不清了啦拜拜我去找爸爸喽说完我便径直走向书房说起爸爸我可是引以为傲的爸爸不仅字写得好而且辅导我作业时也没有什么能难得到他解起题来易如反掌更令我自豪的是爸爸是永远追随着我的爸爸告诉我我和妈妈谁聪明谁漂亮你更喜欢谁一连串的问题像炮弹击中爸爸而爸爸却只说当然是你就短短一句话融化了我的心深深感受到了爸爸的爱这就是我的家一个充满欢声笑语的美好的家我的家我爱他亲爱的奶奶你离我远去已经有八年多了可我依然好想念你那时候的你不是说你是要去美国淘金吗还信誓旦旦地跟我说要把黄金带回来给我年少幼稚的我还真把这事当真了一直期待着你回来直到两个星期后你回来了你的确回来了不过是躺着回来的身体还用白布盖着那时候我刚放学进入门口的那一刻我呆了愣愣地在原地站着屋子里面充满了哭声闹声念咒声可我的世界是一片寂静的里面人们来来往往地忙碌着可我的眼中就只有你你安详地躺在那里面无表情化妆师把你的两腮弄得粉红粉红的头发也整齐地束起来衣服也换上了新衣整个人看起来似乎是那么地精神可是你为什么要躺着呢你为什么要紧闭双眼呢眼泪已经在眶里打转我仍然不相信眼前的这一切后来我妈妈注意到我回来了带着那红红的双眼朝我走来用沙哑的声音对我说奶奶走了快去祭拜一下送她最后一程吧我身体像被抽了灵魂一样站在原地无法动弹直到妈妈将我拖过去跪在你的面前我再也控制不了我的眼泪了哗的一声犹如瀑布直流而下奶奶你回来好不好这是我最想说出的一句话可是当时作文伤心欲绝的我已经出不了声了奶奶这八年来我无时无刻不想念你是你在我小时候孤独无聊的时候陪在我身边的是你在我肚子饿的时候为我煮上一碗热乎乎的汤面是你在我生病难受的时候在身边无微不至的照顾我是你在我顽皮不肯吃饭的时候有耐心的哄我是你是你我童年的一切一切都有你的存在因为有你我的童年才圆满以前有你在我就感到很安全还记得那次吗有两个大哥哥欺负我把我的玩具抢走了藏起来我哭着回家找你那时候的你应该很慌张吧你耐心地哄我睡了之后居然爬楼梯一层一层地帮我把玩具找回来睡醒之后你已拿着玩具出现在我面前很开心地对我说宝贝玩具找到了开不开心呀我着急地抢了玩具就去一边玩了现在想起来感觉真不孝呀奶奶你在天国好吗吃饱喝足了吗还有没有为像我那么淘气的孩子而担忧呀不过我相信你现在一定生活得很幸福因为你是一位好奶奶我呀生活得不错现在社会日渐发展生活质量都比以前好很多了你不用再过多地担心我了我能好好的照顾自己了愿您快乐无忧愿我一切平安你的孙女";
                    abc = shuffleString(abc);
    
                    NBTTagList pages = new NBTTagList();
    
                    for (int page = 0; page < limit; page++) {
                        pages.appendTag(new NBTTagString(abc.substring(page * 156, (page + 1) * 156)));
                    }
    
                    if (heldItem.hasTagCompound()) {
                        heldItem.getTagCompound().setTag("pages", pages);
                    } else {
                        heldItem.setTagInfo("pages", pages);
                    }
                    
                    heldItem.setTagInfo("author", new NBTTagString("i_am_bot"));
                    heldItem.setTagInfo("title",new NBTTagString(String.valueOf(this.randObj.nextInt(99999))));
    
                    PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
                    buf.writeItemStack(heldItem);
    
                    player.connection.sendPacket(new CPacketCustomPayload("MC|BSign", buf));
                    
                    this.d += 1;
                    this.tickCounter = 0;
                }
    
                if(this.d >= this.inventorySize){
                    this.mSender.sendMessage(new TextComponentTranslation("commands.cbook.success"));
    
                    this.d = 0;
                    this.doBook = false;
                }
            }
        }
    }


      private static class RandomNkzlxs{
        public boolean doBook = false;
        public IntStream characterGenerator;
        
        
        private int tickGap = 10;
        private int tickCounter = 0;
        private int d = 0;
        private int inventorySize = 9;
        private ICommandSender mSender = null;
        private String[] mArgs;
        private Random randObj;


        Minecraft MC = FMLClientHandler.instance().getClient();
        

        public RandomNkzlxs(MinecraftServer server, ICommandSender sender, String[] args){
            this.doBook = true; 
            this.mSender = sender;
            this.mArgs = args;
            this.randObj = new Random();
            sender.sendMessage(new TextComponentTranslation("RandomNkzlxs() initiated!"));
        }

        public void increaseTick() throws CommandException{
            if(this.doBook){
                this.tickCounter += 1;
                MC.player.inventory.currentItem = this.d;
                if(this.tickCounter % this.tickGap == 0){

                    this.mSender.sendMessage(new TextComponentTranslation(String.format("tickCounter = %d",this.tickCounter)));
                        
                    this.characterGenerator = this.randObj.ints(0x80, 0x10ffff - 0x800).map(i -> i < 0xd800 ? i : i + 0x800);


                    if (!(this.mSender instanceof EntityPlayerSP))
                        throw new CommandException("commands.cbook.noPlayer");
    
                    int limit = this.mArgs.length > 1 ? parseInt(this.mArgs[1], 1, 50) : 50;
    
                    EntityPlayerSP player = (EntityPlayerSP) this.mSender;
    
                    ItemStack heldItem = player.inventory.getStackInSlot(this.d);
                    if (heldItem.getItem() != Items.WRITABLE_BOOK) {
                        throw new CommandException("commands.cbook.noBook");
                    }
    
                    String joinedPages = this.characterGenerator.limit(50 * 210).mapToObj(i -> String.valueOf((char) i)).collect(Collectors.joining());

                    NBTTagList pages = new NBTTagList();

                    for (int page = 0; page < limit; page++) {
                        pages.appendTag(new NBTTagString(joinedPages.substring(page * 210, (page + 1) * 210)));
                    }

                    if (heldItem.hasTagCompound()) {
                        heldItem.getTagCompound().setTag("pages", pages);
                    } else {
                        heldItem.setTagInfo("pages", pages);
                    }
                    
                    heldItem.setTagInfo("author", new NBTTagString("i_am_bot"));
                    heldItem.setTagInfo("title",new NBTTagString(String.format("randNkz_%s",String.valueOf(this.randObj.nextInt(99999)))));
    
                    PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
                    buf.writeItemStack(heldItem);
    
                    player.connection.sendPacket(new CPacketCustomPayload("MC|BSign", buf));
                    
                    this.d += 1;
                    this.tickCounter = 0;
                }
    
                if(this.d >= this.inventorySize){
                    this.mSender.sendMessage(new TextComponentTranslation("commands.cbook.success"));
    
                    this.d = 0;
                    this.doBook = false;
                }
            }
        }
    }
}