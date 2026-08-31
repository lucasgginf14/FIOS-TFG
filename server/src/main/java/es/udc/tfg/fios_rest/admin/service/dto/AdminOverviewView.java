package es.udc.tfg.fios_rest.admin.service.dto;

public record AdminOverviewView(
  long usersCount,
  long musicalSpacesCount,
  long reservationsCount,
  long spaceReviewsCount,
  long userReviewsCount,
  long eventsCount,
  long bandRecruitmentsCount
) {
}
