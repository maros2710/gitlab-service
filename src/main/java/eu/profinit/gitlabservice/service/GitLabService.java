package eu.profinit.gitlabservice.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import eu.profinit.gitlabservice.controller.dto.UserWithProjects;
import eu.profinit.gitlabservice.database.DatabaseService;
import eu.profinit.gitlabservice.database.model.GitLabProject;
import eu.profinit.gitlabservice.database.model.GitLabUser;
import eu.profinit.gitlabservice.gitlab.GitLabClient;
import eu.profinit.gitlabservice.gitlab.dto.GitLabProjectDto;
import eu.profinit.gitlabservice.gitlab.dto.GitLabUserDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GitLabService {

  private final GitLabClient gitLabClient;
  private final DatabaseService databaseService;
  private final SimpleDateFormat SDF = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
  private final int CACHE_TIME_30_SECONDS = 30 * 1000;

  public UserWithProjects getUser(final String username) {
    final Optional<GitLabUser> user = databaseService.getUser(username);
    if (user.isPresent()) {
      if (new Date().getTime() - user.get().getUpdated().getTime() > CACHE_TIME_30_SECONDS) {
        return refreshDatabaseCache(username);
      } else {
        new UserWithProjects(
                user.get(), databaseService.getProjects(username), SDF.format(new Date()));
      }
    } else {
      return refreshDatabaseCache(username);
    }
    return null;
  }

  @Transactional
  public UserWithProjects refreshDatabaseCache(final String username) {
    final GitLabUserDto userDto = gitLabClient.getUser(username);
    final List<GitLabProjectDto> projectsDto = gitLabClient.getUserProjects(username);

    final GitLabUser user = databaseService.saveUser(userDto);
    final List<GitLabProject> projects =
            projectsDto.stream().map(p -> databaseService.saveProject(userDto, p)).toList();

    return new UserWithProjects(user, projects, SDF.format(new Date()));
  }
}
