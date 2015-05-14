package sk.tomsik68.mclauncher.impl.login.legacy;

import sk.tomsik68.mclauncher.api.common.MCLauncherAPI;
import sk.tomsik68.mclauncher.api.login.IProfile;
import sk.tomsik68.mclauncher.api.login.IProfileIO;

import java.io.*;

public class LegacyProfileIO implements IProfileIO {
    private final LegacyLoginEncryptionProcessor proc;
    private final File dest;

    public LegacyProfileIO(File mcInstance) {
        proc = new LegacyLoginEncryptionProcessor();
        dest = new File(mcInstance, "lastlogin");
    }

    @Override
    public IProfile[] read() throws Exception {
        String user, pass;
        DataInputStream input = new DataInputStream(proc.decrypt(new FileInputStream(dest)));
        user = input.readUTF();
        pass = input.readUTF();
        input.close();
        return new IProfile[]{new LegacyProfile(user, pass)};
    }

    @Override
    public void write(IProfile[] profile) throws Exception {
        if(profile.length > 1){
            MCLauncherAPI.log.warning("Saving multiple profiles using LegacyProfileIO is not possible! MCLauncherAPI will only save the one that is 0th in the array. Other profiles won't be saved!");
        }
        if (!dest.exists()) {
            dest.getParentFile().mkdirs();
            dest.createNewFile();
        }
        DataOutputStream out = new DataOutputStream(proc.encrypt(new FileOutputStream(dest)));
        out.writeUTF(profile[0].getName());
        out.writeUTF(profile[0].getPassword());
        out.flush();
        out.close();
    }

}