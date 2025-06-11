package com.dailycodework.universalpetcare.service.photo;

import com.dailycodework.universalpetcare.exception.ResourceNotFoundException;
import com.dailycodework.universalpetcare.model.Photo;
import com.dailycodework.universalpetcare.model.User;
import com.dailycodework.universalpetcare.repository.PhotoRepository;
import com.dailycodework.universalpetcare.repository.UserRepository;
import com.dailycodework.universalpetcare.utils.FeedBackMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PhotoService implements IPhotoService{
    private final PhotoRepository photoRepository;
    private final UserRepository userRepository;

    @Override
    public Photo savePhoto(MultipartFile file, Long userId) throws IOException, SQLException {
        Optional<User> theUser = userRepository.findById(userId);
        Photo photo = new Photo();
        if(file != null && !file.isEmpty()){
            byte[] photoBytes = file.getBytes();
            Blob photoBlob = new SerialBlob(photoBytes);
            photo.setImage(photoBlob);
            photo.setFileType(file.getContentType());
            photo.setFileName(file.getOriginalFilename());
        }
        Photo savedPhoto = photoRepository.save(photo);
        theUser.ifPresent(user -> {user.setPhoto(savedPhoto);});
        userRepository.save(theUser.get());
        return savedPhoto;
    }

    @Override
    public Photo getPhotoById(Long id) {
        return photoRepository.findById(id).orElse(null);
    }

    @Override
    public void deletePhoto(Long id) {
        photoRepository.findById(id).ifPresentOrElse(photoRepository::delete, () -> {throw new ResourceNotFoundException(FeedBackMessage.NOT_FOUND);});
    }

    @Override
    public Photo updatePhoto(Long id, MultipartFile file) throws SQLException, IOException {
        Photo photo = getPhotoById(id);
        if(photo != null){
            byte[] photoBytes = file.getBytes();
            Blob photoBlob = new SerialBlob(photoBytes);
            if(!file.isEmpty()) {
                photo.setImage(photoBlob);
                photo.setFileType(file.getContentType());
                photo.setFileName(file.getOriginalFilename());
            }
            return photoRepository.save(photo);
        }else {
            throw new ResourceNotFoundException(FeedBackMessage.NOT_FOUND);
        }
    }

    @Override
    public byte[] getPhotoData(Long id) throws SQLException {
        Optional<Photo> photo = Optional.ofNullable(getPhotoById(id));
        if(photo.isPresent()){
            Blob photoBlob = photo.get().getImage();
            int blobLength = (int) photoBlob.length();
            return new byte[blobLength];
        }
        return null;
    }
}
